package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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
        portfolioHoldingService.recalculatePortfolioHolding(holdingId,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
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
    public TradeDto editTrade(EditTradeDto dto) {
        Trade trade = tradeRepository.getById(dto.getTradeId());
        trade.setDate(trade.getDate().equals(dto.getDate()) ? trade.getDate() : dto.getDate());
        trade.setPrice(trade.getPrice().equals(dto.getPrice()) ? trade.getPrice() : dto.getPrice());
        trade.setQuantity(trade.getQuantity().equals(dto.getQuantity()) ? trade.getQuantity() : dto.getQuantity());
        trade.setBroker(trade.getBroker().getId().equals(dto.getBrokerId()) ? trade.getBroker() : brokerRepository.getById(dto.getBrokerId()));
        var updatedTrade = tradeRepository.save(trade);
        Long holdingId = trade.getPortfolioHolding().getId();
        portfolioHoldingService.recalculatePortfolioHolding(holdingId,
                tradeRepository.findAllByPortfolioHolding_IdOrderByDate(holdingId));
        return TradeDto.builder()
                .id(updatedTrade.getId())
                .build();
    }
}
