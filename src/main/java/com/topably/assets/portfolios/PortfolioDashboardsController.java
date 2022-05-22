package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.dto.PortfolioDashboardDto;
import com.topably.assets.portfolios.service.PortfolioDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/portfolio-dashboards")
@RequiredArgsConstructor
public class PortfolioDashboardsController {

    private final PortfolioDashboardService portfolioDashboardService;

    @GetMapping
    public PortfolioDashboardDto getUserPortfolioDashboard(Principal principal) {
        return this.portfolioDashboardService.findUserPortfolioDashboard(principal.getName());
    }
}
