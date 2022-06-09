package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final TradeViewRepository tradeViewRepository;
    private final BrokerRepository brokerRepository;

    private final PortfolioHoldingService portfolioHoldingService;

    @Override
    public Collection<Trade> findDividendPayingTrades(Long portfolioId) {
        return tradeRepository.findDividendPayingTradesOrderByTradeDate(portfolioId);
    }

    @Override
    @Transactional
    public TradeDto addTrade(AddTradeDto dto, Instrument tradedInstrument) {
        PortfolioHolding holding = portfolioHoldingService.findByUserIdAndInstrumentId(dto.getUserId(), dto.getInstrumentId())
                .orElseGet(() -> portfolioHoldingService.createHolding(dto, tradedInstrument));
        var trade = Trade.builder()
                .portfolioHolding(holding)
                .operation(dto.getOperation())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .broker(brokerRepository.getById(dto.getBrokerId()))
                .build();
        var savedTrade = tradeRepository.save(trade);
        Long holdingId = holding.getId();
        AggregatedTradeDto aggregateTrade = aggregateTrades(tradedInstrument,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        portfolioHoldingService.updatePortfolioHolding(holdingId, aggregateTrade);
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
    }

    private AggregatedTradeDto aggregateTrades(Instrument tradedInstrument, Collection<Trade> tradesOrderedByDate) {
        var buyTradesData = new LinkedList<AbstractMap.SimpleEntry<BigInteger, BigDecimal>>();
        var closedPnl = BigDecimal.ZERO;
        for (Trade trade : tradesOrderedByDate) {
            TradeOperation operation = trade.getOperation();
            if (buyTradesData.isEmpty() && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (buyTradesData.isEmpty() || TradeOperation.BUY.equals(operation)) {
                buyTradesData.add(new AbstractMap.SimpleEntry<>(trade.getQuantity(), trade.getPrice()));
                continue;
            }
            if (TradeOperation.SELL.equals(operation)) {
                var buySharesByPrice = buyTradesData.poll();
                closedPnl = closedPnl.add(calculatePnl(buySharesByPrice.getKey(), buySharesByPrice.getValue(),
                        trade.getQuantity(), trade.getPrice()));
                if (trade.getQuantity().compareTo(buySharesByPrice.getKey()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(buySharesByPrice.getKey());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextBuySharesByPrice = buyTradesData.poll();
                        if (nextBuySharesByPrice == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        closedPnl = closedPnl.add(calculatePnl(nextBuySharesByPrice.getKey(), nextBuySharesByPrice.getValue(),
                                remainingSharesToSell, trade.getPrice()));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextBuySharesByPrice.getKey());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.add(new AbstractMap.SimpleEntry<>(remainingSharesToSell.negate(),
                                    nextBuySharesByPrice.getValue()));
                        }
                    }
                } else if (trade.getQuantity().compareTo(buySharesByPrice.getKey()) < 0) {
                    buyTradesData.add(new AbstractMap.SimpleEntry<>(buySharesByPrice.getKey().subtract(trade.getQuantity()),
                            buySharesByPrice.getValue()));
                }
            } else {
                throw new RuntimeException(String.format("Operation %s is not supported", operation.name()));
            }
        }
        var sharesByAvgPrice = buyTradesData.stream().collect(Collectors.teeing(
                Collectors.reducing(BigInteger.ZERO, AbstractMap.SimpleEntry::getKey, BigInteger::add),
                Collectors.reducing(BigDecimal.ZERO, t -> t.getValue().multiply(new BigDecimal(t.getKey())), BigDecimal::add),
                (qty, total) -> new AbstractMap.SimpleEntry<>(qty, BigInteger.ZERO.equals(qty) ? BigDecimal.ZERO :
                        total.divide(new BigDecimal(qty), 4, RoundingMode.HALF_UP))
        ));

        return AggregatedTradeDto.builder()
                .identifier(tradedInstrument.toTickerSymbol())
                .quantity(sharesByAvgPrice.getKey())
                .price(sharesByAvgPrice.getValue())
                .currency(tradedInstrument.getExchange().getCurrency())
                .build();
    }

    private BigDecimal calculatePnl(BigInteger buySideShares, BigDecimal buyPrice,
                                    BigInteger sellSideShares, BigDecimal sellPrice) {
        BigInteger sharesDelta = buySideShares.subtract(sellSideShares);
        var pnlShares = new BigDecimal(sharesDelta.compareTo(BigInteger.ZERO) >= 0 ? sellSideShares : buySideShares);
        BigDecimal nextTotal = buyPrice.multiply(pnlShares);
        return sellPrice.multiply(pnlShares).subtract(nextTotal);
    }

    @Override
    public Collection<TradeView> getUserTrades(String username) {
        return tradeViewRepository.findByUsername(username);
    }

    @Override
    public Collection<Trade> findTradesByUserId(Long userId) {
        return tradeRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public TradeDto editTrade(EditTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        trade.setDate(trade.getDate().equals(dto.getDate()) ? trade.getDate() : dto.getDate());
        trade.setPrice(trade.getPrice().equals(dto.getPrice()) ? trade.getPrice() : dto.getPrice());
        trade.setQuantity(trade.getQuantity().equals(dto.getQuantity()) ? trade.getQuantity() : dto.getQuantity());
        trade.setBroker(trade.getBroker().getId().equals(dto.getBrokerId()) ? trade.getBroker() : brokerRepository.getById(dto.getBrokerId()));
        var updatedTrade = tradeRepository.save(trade);
        Long holdingId = trade.getPortfolioHolding().getId();
        //TODO add tradedInstrument to method signature
        AggregatedTradeDto aggregatedTrade = aggregateTrades(tradedInstrument,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        return TradeDto.builder()
                .id(updatedTrade.getId())
                .build();
    }
}
