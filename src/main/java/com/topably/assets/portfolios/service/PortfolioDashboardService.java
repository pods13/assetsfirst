package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;

public interface PortfolioDashboardService {

    PortfolioDashboardDto findPortfolioDashboardByUserId(Long userId);
}
