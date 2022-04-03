package com.topably.assets.portfolios.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.AddPortfolioDto;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Collection<PortfolioDto> findUserPortfolios(String username) {
        User user = userService.findByUsername(username);
        Collection<Portfolio> portfolios = portfolioRepository.findByUserId(user.getId());
        return portfolios.stream()
                .map(p -> {
                    return PortfolioDto.builder()
                            .id(p.getId())
                            .cards(p.getCards())
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PortfolioDto addPortfolio(String username, AddPortfolioDto dto) {
        User user = userService.findByUsername(username);
        var portfolio = Portfolio.builder()
                .user(user)
                .cards(dto.getCards())
                .build();
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return PortfolioDto.builder()
                .id(savedPortfolio.getId())
                .cards(savedPortfolio.getCards())
                .build();
    }
}
