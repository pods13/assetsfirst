package com.topably.assets.portfolios.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioHoldingServiceImpl implements PortfolioHoldingService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    public Optional<PortfolioHolding> findByUserIdAndInstrumentId(Long userId, Long instrumentId) {
        return portfolioHoldingRepository.findByPortfolio_User_IdAndInstrument_Id(userId, instrumentId);
    }

    @Override
    public PortfolioHolding updatePortfolioHolding(Long holdingId, AggregatedTradeDto dto) {
        PortfolioHolding holding = portfolioHoldingRepository.getById(holdingId);
        holding.setQuantity(dto.getQuantity());
        holding.setAveragePrice(dto.getPrice());
        return portfolioHoldingRepository.save(holding);
    }

    @Override
    public PortfolioHolding createHolding(AddTradeDto dto, Instrument instrument) {
        Portfolio portfolio = portfolioRepository.findByUserId(dto.getUserId());
        return portfolioHoldingRepository.saveAndFlush(PortfolioHolding.builder()
                .portfolio(portfolio)
                .instrument(instrument)
                .quantity(dto.getQuantity())
                .averagePrice(dto.getPrice())
                .build());
    }

    @Override
    public Collection<PortfolioHoldingDto> findPortfolioHoldings(Long portfolioId) {
        return portfolioHoldingRepository.findAllByPortfolioId(portfolioId).stream()
                .map(holding -> {
                    Instrument instrument = holding.getInstrument();
                    return PortfolioHoldingDto.builder()
                            .instrumentId(instrument.getId())
                            .instrumentType(instrument.getInstrumentType())
                            .identifier(instrument.toTickerSymbol())
                            .currency(instrument.getExchange().getCurrency())
                            .quantity(holding.getQuantity())
                            .price(holding.getAveragePrice())
                            .build();
                }).toList();
    }

    @Override
    @Transactional
    public Collection<PortfolioHoldingDto> findPortfolioHoldingsByUserId(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        return findPortfolioHoldings(portfolio.getId());
    }
}
