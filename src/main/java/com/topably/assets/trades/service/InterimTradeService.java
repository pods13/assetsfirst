package com.topably.assets.trades.service;

import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class InterimTradeService {

    public InterimTradeResult calculate(Collection<Trade> tradesOrderedByDate) {
        var buyTradesData = new LinkedList<TradeData>();
        var closedPnl = BigDecimal.ZERO;
        for (Trade trade : tradesOrderedByDate) {
            TradeOperation operation = trade.getOperation();
            if (buyTradesData.isEmpty() && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (buyTradesData.isEmpty() || TradeOperation.BUY.equals(operation)) {
                buyTradesData.add(new TradeData(trade.getQuantity(), trade.getPrice(), trade.getDate()));
                continue;
            }
            if (TradeOperation.SELL.equals(operation)) {
                var tradeData = buyTradesData.poll();
                closedPnl = closedPnl.add(calculatePnl(tradeData.shares(), tradeData.price(),
                    trade.getQuantity(), trade.getPrice()));
                if (trade.getQuantity().compareTo(tradeData.shares()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(tradeData.shares());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextTradeData = buyTradesData.poll();
                        if (nextTradeData == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        closedPnl = closedPnl.add(calculatePnl(nextTradeData.shares(), nextTradeData.price(),
                            remainingSharesToSell, trade.getPrice()));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextTradeData.shares());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.add(new TradeData(remainingSharesToSell.negate(),
                                nextTradeData.price(), nextTradeData.tradeTime()));
                        }
                    }
                } else if (trade.getQuantity().compareTo(tradeData.shares()) < 0) {
                    buyTradesData.add(new TradeData(tradeData.shares().subtract(trade.getQuantity()),
                        tradeData.price(), trade.getDate()));
                }
            } else {
                throw new RuntimeException(String.format("Operation %s is not supported", operation.name()));
            }
        }
        return new InterimTradeResult(buyTradesData, closedPnl);
    }

    private BigDecimal calculatePnl(BigInteger buySideShares, BigDecimal buyPrice,
                                    BigInteger sellSideShares, BigDecimal sellPrice) {
        BigInteger sharesDelta = buySideShares.subtract(sellSideShares);
        var pnlShares = new BigDecimal(sharesDelta.compareTo(BigInteger.ZERO) >= 0 ? sellSideShares : buySideShares);
        BigDecimal nextTotal = buyPrice.multiply(pnlShares);
        return sellPrice.multiply(pnlShares).subtract(nextTotal);
    }

    public record InterimTradeResult(Collection<TradeData> buyTradesData, BigDecimal closedPnl) {
    }

    public record TradeData(BigInteger shares, BigDecimal price, LocalDate tradeTime) {
    }
}
