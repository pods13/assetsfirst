package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.Portfolio;

import java.math.BigDecimal;

public interface PortfolioService {
    Portfolio createDefaultUserPortfolio(Long userId);

    Portfolio findByUsername(String username);
}
