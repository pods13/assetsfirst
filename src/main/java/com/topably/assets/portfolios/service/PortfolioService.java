package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.dto.AddPortfolioDto;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;

import java.util.Collection;

public interface PortfolioService {

    Collection<PortfolioDto> findUserPortfolios(String username);

    PortfolioDto addPortfolio(String username, AddPortfolioDto dto);

}
