package com.topably.assets.trades.service;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.PositionManagementService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.event.TradeChangedEvent;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeManagementService {

    private final TradeRepository tradeRepository;
    private final BrokerRepository brokerRepository;
    private final TradeAggregatorService tradeAggregatorService;
    private final PositionManagementService positionManagementService;
    private final ApplicationEventPublisher eventPublisher;

    public TradeDto addTrade(AddTradeDto dto, Instrument tradedInstrument) {
        var position = positionManagementService.findByUserIdAndInstrumentId(dto.getUserId(), dto.getInstrumentId())
            .orElseGet(() -> positionManagementService.createPosition(dto, tradedInstrument));
        var trade = new Trade()
            .setPortfolioPosition(position)
            .setOperation(dto.getOperation())
            .setPrice(dto.getPrice())
            .setQuantity(dto.getQuantity())
            .setDate(dto.getDate())
            .setBroker(brokerRepository.getReferenceById(dto.getBrokerId()));
        var savedTrade = tradeRepository.saveAndFlush(trade);
        Long positionId = position.getId();
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByPositionId(positionId);
        positionManagementService.updatePortfolioPosition(positionId, aggregatedTrade);
        eventPublisher.publishEvent(new TradeChangedEvent(this));
        return TradeDto.builder()
            .id(savedTrade.getId())
            .build();
    }

    public TradeDto editTrade(EditTradeDto dto, Instrument tradedInstrument) {
        //TODO if the first trade is updated we need to update position openDate as well
        Trade trade = tradeRepository.getReferenceById(dto.getTradeId());
        trade.setDate(trade.getDate().equals(dto.getDate()) ? trade.getDate() : dto.getDate());
        trade.setPrice(trade.getPrice().equals(dto.getPrice()) ? trade.getPrice() : dto.getPrice());
        trade.setQuantity(trade.getQuantity().equals(dto.getQuantity()) ? trade.getQuantity() : dto.getQuantity());
        trade.setBroker(trade.getBroker().getId().equals(dto.getBrokerId()) ? trade.getBroker() : brokerRepository.getReferenceById(dto.getBrokerId()));
        var updatedTrade = tradeRepository.saveAndFlush(trade);
        Long positionId = trade.getPortfolioPosition().getId();
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByPositionId(positionId);
        positionManagementService.updatePortfolioPosition(positionId, aggregatedTrade);
        eventPublisher.publishEvent(new TradeChangedEvent(this));
        return TradeDto.builder()
            .id(updatedTrade.getId())
            .build();
    }

    public void deleteTrade(DeleteTradeDto dto, Instrument tradedInstrument) {
        Trade trade = tradeRepository.getReferenceById(dto.getTradeId());
        Long positionId = trade.getPortfolioPosition().getId();
        tradeRepository.delete(trade);
        tradeRepository.flush();
        var aggregatedTrade = tradeAggregatorService.aggregateTradesByPositionId(positionId);
        if (BigInteger.ZERO.equals(aggregatedTrade.getQuantity())) {
            positionManagementService.deletePortfolioPosition(positionId);
        } else {
            positionManagementService.updatePortfolioPosition(positionId, aggregatedTrade);
        }
        eventPublisher.publishEvent(new TradeChangedEvent(this));
    }
}
