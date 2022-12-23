package com.topably.assets.trades.service;

import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
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
import java.util.Map;
import java.util.stream.Collectors;

import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.BrokerData;
import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.InterimTradeResult;
import static com.topably.assets.trades.domain.dto.AggregatedTradeDto.TradeData;

@Service
@RequiredArgsConstructor
public class TradeAggregatorService {

    public AggregatedTradeDto aggregateTrades(Collection<TradeView> tradesOrderedByDate) {
        var tradeResult = calculateInterimResult(tradesOrderedByDate);
        var sharesByAvgPrice = tradeResult.buyTradesData().values().stream().flatMap(Collection::stream)
            .collect(Collectors.teeing(
                Collectors.reducing(BigInteger.ZERO, TradeData::getShares, BigInteger::add),
                Collectors.reducing(BigDecimal.ZERO, t -> t.getPrice().multiply(new BigDecimal(t.getShares())), BigDecimal::add),
                (qty, total) -> new AbstractMap.SimpleEntry<>(qty, BigInteger.ZERO.equals(qty) ? BigDecimal.ZERO :
                    total.divide(new BigDecimal(qty), 4, RoundingMode.HALF_UP))
            ));

        return new AggregatedTradeDto()
            .setQuantity(sharesByAvgPrice.getKey())
            .setPrice(sharesByAvgPrice.getValue())
            .setClosedPnl(tradeResult.closedPnl())
            .setBuyTradesData(tradeResult.buyTradesData());
    }

    private InterimTradeResult calculateInterimResult(Collection<TradeView> tradesOrderedByDate) {
        var buyTradesData = new LinkedHashMap<BrokerData, LinkedList<TradeData>>();
        var closedPnl = BigDecimal.ZERO;
        for (var trade : tradesOrderedByDate) {
            var broker = new BrokerData(trade.getBrokerId(), trade.getBrokerName());
            var operation = trade.getOperation();
            if (!buyTradesData.containsKey(broker) && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (TradeOperation.BUY.equals(operation)) {
                if (!buyTradesData.containsKey(broker)) {
                    buyTradesData.put(broker, new LinkedList<>());
                }
                buyTradesData.get(broker).add(new TradeData(trade.getQuantity(), trade.getPrice(), trade.getDate(), trade));
                continue;
            }
            if (TradeOperation.SELL.equals(operation) && buyTradesData.containsKey(broker) && !buyTradesData.get(broker).isEmpty()) {
                var tradeData = buyTradesData.get(broker).poll();
                closedPnl = closedPnl.add(calculatePnl(tradeData.getShares(), tradeData.getPrice(),
                    trade.getQuantity(), trade.getPrice()));
                if (trade.getQuantity().compareTo(tradeData.getShares()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(tradeData.getShares());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextTradeData = buyTradesData.get(broker).poll();
                        if (nextTradeData == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        closedPnl = closedPnl.add(calculatePnl(nextTradeData.getShares(), nextTradeData.getPrice(),
                            remainingSharesToSell, trade.getPrice()));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextTradeData.getShares());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.get(broker).add(new TradeData(remainingSharesToSell.negate(),
                                nextTradeData.getPrice(), nextTradeData.getTradeTime(), trade));
                        }
                    }
                } else if (trade.getQuantity().compareTo(tradeData.getShares()) < 0) {
                    buyTradesData.get(broker).add(new TradeData(tradeData.getShares().subtract(trade.getQuantity()),
                        tradeData.getPrice(), trade.getDate(), trade));
                }
            } else {
                throw new RuntimeException(String.format("Operation %s is not supported", operation.name()));
            }
        }
        var buyTrades = buyTradesData.entrySet().stream()
            .filter(e -> !e.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
