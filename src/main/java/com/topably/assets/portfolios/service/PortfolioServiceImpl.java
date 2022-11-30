package com.topably.assets.portfolios.service;

import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;

    private final UserService userService;

    @Override
    @Transactional
    public Portfolio createDefaultUserPortfolio(Long userId) {
        var dashboard = PortfolioDashboard.builder()
            .cards(new HashSet<>())
            .build();
        Portfolio portfolio = Portfolio.builder()
            .user(userService.getById(userId))
            .dashboard(dashboard)
            .build();
        return portfolioRepository.save(portfolio);
    }

    public Portfolio findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
}
