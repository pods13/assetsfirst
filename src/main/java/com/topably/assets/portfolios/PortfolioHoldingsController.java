package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.PortfolioHoldingView;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/portfolio-holdings")
@RequiredArgsConstructor
public class PortfolioHoldingsController {

    private final PortfolioHoldingService portfolioHoldingService;

    @GetMapping
    public Collection<PortfolioHoldingDto> getPortfolioHoldings(@AuthenticationPrincipal CurrentUser user) {
        return portfolioHoldingService.findPortfolioHoldingsByUserId(user.getUserId());
    }

    @GetMapping("/view")
    public Collection<PortfolioHoldingView> getPortfolioHoldingsView(@AuthenticationPrincipal CurrentUser user) {
        return portfolioHoldingService.findPortfolioHoldingsView(user.getUserId());
    }
}
