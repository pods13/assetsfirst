package com.topably.assets.portfolios.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.PortfolioHoldingView;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;

import java.util.Collection;
import java.util.Optional;

public interface PortfolioHoldingService {

    Optional<PortfolioHolding> findByUserIdAndInstrumentId(Long userId, Long instrumentId);

    PortfolioHolding updatePortfolioHolding(Long holdingId, AggregatedTradeDto dto);

    PortfolioHolding createHolding(AddTradeDto dto, Instrument instrument);

    Collection<PortfolioHoldingDto> findPortfolioHoldings(Long portfolioId);

    Collection<PortfolioHoldingDto> findPortfolioHoldingsByUserId(Long userId);

    Collection<PortfolioHoldingView> findPortfolioHoldingsView(Long userId);
}
