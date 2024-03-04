package com.topably.assets.trades.service.manage;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.repository.tag.TagRepository;
import com.topably.assets.portfolios.service.PositionManagementService;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import com.topably.assets.trades.domain.dto.manage.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.manage.EditTradeDto;
import com.topably.assets.trades.domain.event.TradeChangedEvent;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.service.TradeAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TradeManagementService {

    private final TradeRepository tradeRepository;
    private final TagRepository tagRepository;
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
            .setIntermediary(tagRepository.getReferenceById(dto.getIntermediaryId()));
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
        trade.setIntermediary(trade.getIntermediary().getId().equals(dto.getIntermediaryId())
            ? trade.getIntermediary()
            : tagRepository.getReferenceById(dto.getIntermediaryId()));
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
        if (aggregatedTrade.getBuyTradesData().isEmpty() && aggregatedTrade.getDeltaPnls().isEmpty()) {
            positionManagementService.deletePortfolioPosition(positionId);
        } else {
            positionManagementService.updatePortfolioPosition(positionId, aggregatedTrade);
        }
        eventPublisher.publishEvent(new TradeChangedEvent(this));
    }

}
