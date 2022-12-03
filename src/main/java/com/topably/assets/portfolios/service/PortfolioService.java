package com.topably.assets.portfolios.service;

import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final UserService userService;

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
