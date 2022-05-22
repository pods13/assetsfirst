package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;

public interface PortfolioService {

    PortfolioDto findUserPortfolio(String username);

    Portfolio createDefaultUserPortfolio(Long userId);
}
