package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Currency;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final TradeViewRepository tradeViewRepository;
    private final BrokerRepository brokerRepository;

    private final PortfolioHoldingService portfolioHoldingService;
    private final CurrencyConverterService currencyConverterService;

    @Override
    public Collection<Trade> findDividendPayingTrades(Long portfolioId) {
        return tradeRepository.findDividendPayingTradesOrderByTradeDate(portfolioId);
    }

    @Override
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
        var tradeResult = calculateInterimTradeResult(tradesOrderedByDate);
        var sharesByAvgPrice = tradeResult.buyTradesData().stream().collect(Collectors.teeing(
                Collectors.reducing(BigInteger.ZERO, TradeData::shares, BigInteger::add),
                Collectors.reducing(BigDecimal.ZERO, t -> t.price().multiply(new BigDecimal(t.shares())), BigDecimal::add),
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

    private record TradeData(BigInteger shares, BigDecimal price, LocalDateTime tradeTime) {
    }

    private InterimTradeResult calculateInterimTradeResult(Collection<Trade> tradesOrderedByDate) {
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

    private record InterimTradeResult(Collection<TradeData> buyTradesData, BigDecimal closedPnl) {
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
    public TradeDto editTrade(EditTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        trade.setDate(trade.getDate().equals(dto.getDate()) ? trade.getDate() : dto.getDate());
        trade.setPrice(trade.getPrice().equals(dto.getPrice()) ? trade.getPrice() : dto.getPrice());
        trade.setQuantity(trade.getQuantity().equals(dto.getQuantity()) ? trade.getQuantity() : dto.getQuantity());
        trade.setBroker(trade.getBroker().getId().equals(dto.getBrokerId()) ? trade.getBroker() : brokerRepository.getById(dto.getBrokerId()));
        var updatedTrade = tradeRepository.save(trade);
        Long holdingId = trade.getPortfolioHolding().getId();
        AggregatedTradeDto aggregatedTrade = aggregateTrades(tradedInstrument,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        return TradeDto.builder()
                .id(updatedTrade.getId())
                .build();
    }

    @Override
    public BigDecimal calculateInvestedAmountByHoldingId(Long holdingId, Currency holdingCurrency, Currency portfolioCurrency) {
        var trades = tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId);
        var tradeResult = calculateInterimTradeResult(trades);
        return tradeResult.buyTradesData().stream()
                .map(t -> {
                    BigDecimal total = t.price().multiply(new BigDecimal(t.shares()));
                    return currencyConverterService.convert(total, holdingCurrency, portfolioCurrency,
                            t.tradeTime().toInstant(ZoneOffset.UTC));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deleteTrade(DeleteTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        Long holdingId = trade.getPortfolioHolding().getId();
        tradeRepository.delete(trade);
        AggregatedTradeDto aggregatedTrade = aggregateTrades(tradedInstrument,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        if (BigInteger.ZERO.equals(aggregatedTrade.getQuantity())) {
            portfolioHoldingService.deletePortfolioHolding(holdingId);
        } else {
            portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        }
    }
}
