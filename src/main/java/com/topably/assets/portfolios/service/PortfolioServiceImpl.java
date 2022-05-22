package com.topably.assets.portfolios.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    @Override
    @Transactional
    public PortfolioDto findUserPortfolio(String username) {
        User user = userService.findByUsername(username);
        var portfolio = portfolioRepository.findByUserId(user.getId());
        return PortfolioDto.builder()
                .id(portfolio.getId())
                .cards(portfolio.getCards())
                .build();
    }

    @Override
    @Transactional
    public Portfolio createDefaultUserPortfolio(Long userId) {
        var portfolio = Portfolio.builder()
                .user(userService.getById(userId))
                .cards(new HashSet<>())
                .build();
        return portfolioRepository.save(portfolio);
    }
}
