package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;
import com.topably.assets.portfolios.service.PortfolioDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio-dashboards")
@RequiredArgsConstructor
public class PortfolioDashboardsController {

    private final PortfolioDashboardService portfolioDashboardService;

    @GetMapping
    public PortfolioDashboardDto getUserPortfolioDashboard(@AuthenticationPrincipal CurrentUser user) {
        return this.portfolioDashboardService.findPortfolioDashboardByUserId(user.getUserId());
    }
}
