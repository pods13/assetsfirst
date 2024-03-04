package com.topably.assets.portfolios.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.portfolios.repository.PortfolioPositionRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionManagementService {

    private final PortfolioPositionRepository portfolioPositionRepository;
    private final PortfolioRepository portfolioRepository;

    public Optional<PortfolioPosition> findByUserIdAndInstrumentId(Long userId, Long instrumentId) {
        return portfolioPositionRepository.findByPortfolio_User_IdAndInstrument_Id(userId, instrumentId);
    }

    public PortfolioPosition createPosition(AddTradeDto dto, Instrument instrument) {
        var portfolio = portfolioRepository.findByUserId(dto.getUserId());
        return portfolioPositionRepository.saveAndFlush(new PortfolioPosition()
            .setPortfolio(portfolio)
            .setInstrument(instrument)
            .setQuantity(dto.getQuantity())
            .setAveragePrice(dto.getPrice())
            .setOpenDate(dto.getDate()));
    }

    public PortfolioPosition updatePortfolioPosition(Long positionId, AggregatedTradeDto dto) {
        var position = portfolioPositionRepository.getReferenceById(positionId);
        position.setQuantity(dto.getQuantity())
            .setAveragePrice(dto.getPrice())
            .setRealizedPnl(dto.getPnl());
        return portfolioPositionRepository.save(position);
    }

    public void deletePortfolioPosition(Long positionId) {
        portfolioPositionRepository.deleteById(positionId);
    }
}
