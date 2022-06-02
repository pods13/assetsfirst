package com.topably.assets.portfolios.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;

import java.util.Collection;
import java.util.Optional;

public interface PortfolioHoldingService {

    Optional<PortfolioHolding> findByUsernameAndInstrumentId(String username, Long instrumentId);

    PortfolioHolding managePortfolioHolding(AddTradeDto dto, Instrument tradedInstrument);

    Collection<PortfolioHoldingDto> findPortfolioHoldings(Long portfolioId);

    Collection<PortfolioHoldingDto> findPortfolioHoldingsByUserId(Long userId);
}
