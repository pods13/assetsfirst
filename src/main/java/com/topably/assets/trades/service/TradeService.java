package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final TradeViewRepository tradeViewRepository;
    private final BrokerRepository brokerRepository;

    private final PortfolioHoldingService portfolioHoldingService;
    private final TradeAggregatorService tradeAggregatorService;

    public Collection<Trade> findDividendPayingTrades(Long portfolioId, Collection<Integer> dividendYears) {
        return tradeRepository.findDividendPayingTradesOrderByTradeDate(portfolioId, dividendYears);
    }

    public TradeDto addTrade(AddTradeDto dto, Instrument tradedInstrument) {
        PortfolioHolding holding = portfolioHoldingService.findByUserIdAndInstrumentId(dto.getUserId(), dto.getInstrumentId())
            .orElseGet(() -> portfolioHoldingService.createHolding(dto, tradedInstrument));
        var trade = new Trade()
            .setPortfolioHolding(holding)
            .setOperation(dto.getOperation())
            .setPrice(dto.getPrice())
            .setQuantity(dto.getQuantity())
            .setDate(dto.getDate())
            .setBroker(brokerRepository.getReferenceById(dto.getBrokerId()));
        var savedTrade = tradeRepository.save(trade);
        Long holdingId = holding.getId();
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByHoldingId(holdingId);
        portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        return TradeDto.builder()
            .id(savedTrade.getId())
            .build();
    }

    @Transactional(readOnly = true)
    public Page<TradeView> getUserTrades(Long userId, Pageable pageable) {
        return tradeViewRepository.findByUserId(userId, pageable);
    }

    public Collection<Trade> findTradesByUserId(Long userId) {
        return tradeRepository.findAllByUserId(userId);
    }

    public TradeDto editTrade(EditTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        trade.setDate(trade.getDate().equals(dto.getDate()) ? trade.getDate() : dto.getDate());
        trade.setPrice(trade.getPrice().equals(dto.getPrice()) ? trade.getPrice() : dto.getPrice());
        trade.setQuantity(trade.getQuantity().equals(dto.getQuantity()) ? trade.getQuantity() : dto.getQuantity());
        trade.setBroker(trade.getBroker().getId().equals(dto.getBrokerId()) ? trade.getBroker() : brokerRepository.getById(dto.getBrokerId()));
        var updatedTrade = tradeRepository.save(trade);
        Long holdingId = trade.getPortfolioHolding().getId();
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByHoldingId(holdingId);
        portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        return TradeDto.builder()
            .id(updatedTrade.getId())
            .build();
    }

    public void deleteTrade(DeleteTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        Long holdingId = trade.getPortfolioHolding().getId();
        tradeRepository.delete(trade);
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByHoldingId(holdingId);
        if (BigInteger.ZERO.equals(aggregatedTrade.getQuantity())) {
            portfolioHoldingService.deletePortfolioHolding(holdingId);
        } else {
            portfolioHoldingService.updatePortfolioHolding(holdingId, aggregatedTrade);
        }
    }

    @Transactional(readOnly = true)
    public Collection<TradeView> getUserTradesForCurrentYear(Portfolio portfolio) {
        var firstDayOfCurrentYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        return tradeViewRepository.findAllByUserIdAndDateGreaterThanEqualOrderByDate(portfolio.getUser().getId(), firstDayOfCurrentYear);
    }
}
