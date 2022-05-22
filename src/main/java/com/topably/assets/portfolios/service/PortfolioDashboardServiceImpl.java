package com.topably.assets.portfolios.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;
import com.topably.assets.portfolios.repository.PortfolioDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PortfolioDashboardServiceImpl implements PortfolioDashboardService {

    private final PortfolioDashboardRepository portfolioDashboardRepository;
    private final UserService userService;

    @Override
    @Transactional
    public PortfolioDashboardDto findUserPortfolioDashboard(String username) {
        User user = userService.findByUsername(username);
        var dashboard = portfolioDashboardRepository.findByUserId(user.getId());
        return PortfolioDashboardDto.builder()
                .id(dashboard.getId())
                .cards(dashboard.getCards())
                .build();
    }

    @Override
    @Transactional
    public PortfolioDashboard createDefaultUserPortfolioDashboard(Long userId) {
        var dashboard = PortfolioDashboard.builder()
                .user(userService.getById(userId))
                .cards(new HashSet<>())
                .build();
        return portfolioDashboardRepository.save(dashboard);
    }
}
