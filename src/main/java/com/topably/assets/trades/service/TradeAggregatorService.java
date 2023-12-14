package com.topably.assets.trades.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto.DeltaPnl;
import com.topably.assets.trades.repository.TradeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.InterimTradeResult;
import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.TradeData;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeAggregatorService {

    private final TradeViewRepository tradeViewRepository;

    public AggregatedTradeDto aggregateTradesByPositionId(Long positionId) {
        var nextDay = LocalDate.now().plus(1, ChronoUnit.DAYS);
        return aggregateTradesByPositionId(positionId, nextDay);
    }

    public AggregatedTradeDto aggregateTradesByPositionId(Long positionId, LocalDate date) {
        var trades = tradeViewRepository.findAllByPositionIdAndDateLessThanEqualOrderByDate(positionId, date);
        return aggregateTrades(trades);
    }

    public AggregatedTradeDto aggregateTrades(Collection<TradeView> tradesOrderedByDate) {
        var tradeResult = calculateInterimResult(tradesOrderedByDate);
        var sharesByAvgPrice = tradeResult.buyTradesData().stream()
            .collect(Collectors.teeing(
                Collectors.reducing(BigInteger.ZERO, TradeData::getShares, BigInteger::add),
                Collectors.reducing(BigDecimal.ZERO, t -> t.getPrice().multiply(new BigDecimal(t.getShares())), BigDecimal::add),
                (qty, total) -> new AbstractMap.SimpleEntry<>(qty, BigInteger.ZERO.equals(qty) ? BigDecimal.ZERO :
                    total.divide(new BigDecimal(qty), 4, RoundingMode.HALF_UP))
            ));

        return new AggregatedTradeDto()
            .setQuantity(sharesByAvgPrice.getKey())
            .setPrice(sharesByAvgPrice.getValue())
            .setDeltaPnls(collapseBySameSellDate(tradeResult.deltaPnls()))
            .setPnl(tradeResult.deltaPnls().stream().map(delta -> delta.totalSell().subtract(delta.totalBuy())).reduce(BigDecimal.ZERO, BigDecimal::add))
            .setBuyTradesData(tradeResult.buyTradesData());
    }

    private List<DeltaPnl> collapseBySameSellDate(List<DeltaPnl> deltaPnls) {
        var dateBySummedTrade = deltaPnls.stream()
            .collect(Collectors.toMap(d -> d.sellDate().toString() + d.buyDate().toString(), Function.identity(),
                (deltaPnl, deltaPnl2) -> new DeltaPnl(deltaPnl.buyDate(), deltaPnl.sellDate(), deltaPnl.totalBuy().add(deltaPnl2.totalBuy()),
                    deltaPnl.totalSell().add(deltaPnl2.totalSell()), deltaPnl.currency())));
        return new ArrayList<>(dateBySummedTrade.values());
    }

    private InterimTradeResult calculateInterimResult(Collection<TradeView> tradesOrderedByDate) {
        var buyTradesData = new LinkedHashMap<Long, LinkedList<TradeData>>();
        var tradePnls = new ArrayList<DeltaPnl>();
        for (var trade : tradesOrderedByDate) {
            var brokerId = trade.getBrokerId();
            var operation = trade.getOperation();
            if (!buyTradesData.containsKey(brokerId) && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (TradeOperation.BUY.equals(operation)) {
                if (!buyTradesData.containsKey(brokerId)) {
                    buyTradesData.put(brokerId, new LinkedList<>());
                }
                buyTradesData.get(brokerId).add(new TradeData(trade.getQuantity(), trade.getPrice(), trade.getDate(), trade));
                continue;
            }
            if (TradeOperation.SELL.equals(operation) && buyTradesData.containsKey(brokerId) && !buyTradesData.get(brokerId).isEmpty()) {
                var tradeData = buyTradesData.get(brokerId).poll();
                tradePnls.add(calculatePnl(tradeData.getTradeTime(), tradeData.getShares(), tradeData.getPrice(), trade.getQuantity(), trade));
                if (trade.getQuantity().compareTo(tradeData.getShares()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(tradeData.getShares());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextTradeData = buyTradesData.get(brokerId).poll();
                        if (nextTradeData == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        tradePnls.add(calculatePnl(nextTradeData.getTradeTime(), nextTradeData.getShares(), nextTradeData.getPrice(),
                            remainingSharesToSell, trade));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextTradeData.getShares());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.get(brokerId).add(new TradeData(remainingSharesToSell.negate(),
                                nextTradeData.getPrice(), nextTradeData.getTradeTime(), trade));
                        }
                    }
                } else if (trade.getQuantity().compareTo(tradeData.getShares()) < 0) {
                    buyTradesData.get(brokerId).add(new TradeData(tradeData.getShares().subtract(trade.getQuantity()),
                        tradeData.getPrice(), trade.getDate(), trade));
                }
            } else {
                throw new RuntimeException(String.format("Operation %s is not supported", operation.name()));
            }
        }
        var buyTrades = buyTradesData.keySet().stream()
            .filter(brokerId -> !buyTradesData.get(brokerId).isEmpty())
            .map(buyTradesData::get)
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(LinkedList::new));
        return new InterimTradeResult(buyTrades, tradePnls);
    }

    private DeltaPnl calculatePnl(LocalDate buyDate,
        BigInteger buySideShares, BigDecimal buyPrice,
        BigInteger sellSideShares, TradeView sellTrade
    ) {
        BigInteger sharesDelta = buySideShares.subtract(sellSideShares);
        var pnlShares = new BigDecimal(sharesDelta.compareTo(BigInteger.ZERO) >= 0 ? sellSideShares : buySideShares);
        BigDecimal nextTotal = buyPrice.multiply(pnlShares);
        return new DeltaPnl(buyDate, sellTrade.getDate(), nextTotal, sellTrade.getPrice().multiply(pnlShares), sellTrade.getCurrency());
    }

}
