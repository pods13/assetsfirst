package com.topably.assets.portfolios.service;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioDashboardServiceImpl implements PortfolioDashboardService {

    private final PortfolioRepository portfolioRepository;

    @Override
    @Transactional
    public PortfolioDashboardDto findUserPortfolioDashboard(String username) {
        Portfolio portfolio = portfolioRepository.findByUser_Username(username);
        return PortfolioDashboardDto.builder()
                .id(portfolio.getDashboard().getId())
                .cards(portfolio.getDashboard().getCards())
                .build();
    }
}
