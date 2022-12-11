package com.topably.assets.trades.service;

import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.InterimTradeResult;
import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.TradeData;

@Service
@RequiredArgsConstructor
public class TradeAggregatorService {

    public AggregatedTradeDto aggregateTrades(Collection<Trade> tradesOrderedByDate) {
        var tradeResult = calculateInterimResult(tradesOrderedByDate);
        var sharesByAvgPrice = tradeResult.buyTradesData().stream().collect(Collectors.teeing(
            Collectors.reducing(BigInteger.ZERO, TradeData::shares, BigInteger::add),
            Collectors.reducing(BigDecimal.ZERO, t -> t.price().multiply(new BigDecimal(t.shares())), BigDecimal::add),
            (qty, total) -> new AbstractMap.SimpleEntry<>(qty, BigInteger.ZERO.equals(qty) ? BigDecimal.ZERO :
                total.divide(new BigDecimal(qty), 4, RoundingMode.HALF_UP))
        ));

        return new AggregatedTradeDto()
            .setQuantity(sharesByAvgPrice.getKey())
            .setPrice(sharesByAvgPrice.getValue())
            .setInterimTradeResult(tradeResult);
    }

    private InterimTradeResult calculateInterimResult(Collection<Trade> tradesOrderedByDate) {
        var buyTradesData = new LinkedHashMap<Long, LinkedList<TradeData>>();
        var closedPnl = BigDecimal.ZERO;
        for (Trade trade : tradesOrderedByDate) {
            var brokerId = trade.getBroker().getId();
            var operation = trade.getOperation();
            if (!buyTradesData.containsKey(brokerId) && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (TradeOperation.BUY.equals(operation)) {
                if (!buyTradesData.containsKey(brokerId)) {
                    buyTradesData.put(brokerId, new LinkedList<>());
                }
                buyTradesData.get(brokerId).add(new TradeData(trade.getQuantity(), trade.getPrice(), trade.getDate()));
                continue;
            }
            if (TradeOperation.SELL.equals(operation) && buyTradesData.containsKey(brokerId) && !buyTradesData.get(brokerId).isEmpty()) {
                var tradeData = buyTradesData.get(brokerId).poll();
                closedPnl = closedPnl.add(calculatePnl(tradeData.shares(), tradeData.price(),
                    trade.getQuantity(), trade.getPrice()));
                if (trade.getQuantity().compareTo(tradeData.shares()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(tradeData.shares());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextTradeData = buyTradesData.get(brokerId).poll();
                        if (nextTradeData == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        closedPnl = closedPnl.add(calculatePnl(nextTradeData.shares(), nextTradeData.price(),
                            remainingSharesToSell, trade.getPrice()));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextTradeData.shares());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.get(brokerId).add(new TradeData(remainingSharesToSell.negate(),
                                nextTradeData.price(), nextTradeData.tradeTime()));
                        }
                    }
                } else if (trade.getQuantity().compareTo(tradeData.shares()) < 0) {
                    buyTradesData.get(brokerId).add(new TradeData(tradeData.shares().subtract(trade.getQuantity()),
                        tradeData.price(), trade.getDate()));
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
        return new InterimTradeResult(buyTrades, closedPnl);
    }

    private BigDecimal calculatePnl(BigInteger buySideShares, BigDecimal buyPrice,
                                    BigInteger sellSideShares, BigDecimal sellPrice) {
        BigInteger sharesDelta = buySideShares.subtract(sellSideShares);
        var pnlShares = new BigDecimal(sharesDelta.compareTo(BigInteger.ZERO) >= 0 ? sellSideShares : buySideShares);
        BigDecimal nextTotal = buyPrice.multiply(pnlShares);
        return sellPrice.multiply(pnlShares).subtract(nextTotal);
    }
}
