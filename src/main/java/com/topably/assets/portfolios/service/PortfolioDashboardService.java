package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;

public interface PortfolioDashboardService {

    PortfolioDashboardDto findUserPortfolioDashboard(String username);
}
