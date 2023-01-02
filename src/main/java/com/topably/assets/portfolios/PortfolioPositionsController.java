package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.PortfolioPositionView;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/portfolio-positions")
@RequiredArgsConstructor
public class PortfolioPositionsController {

    private final PortfolioPositionService portfolioPositionService;

    @GetMapping
    public Collection<PortfolioPositionDto> getPortfolioPositions(@AuthenticationPrincipal CurrentUser user) {
        return portfolioPositionService.findPortfolioPositionsByUserId(user.getUserId());
    }

    @GetMapping("/view")
    public Collection<PortfolioPositionView> getPortfolioPositionsView(@AuthenticationPrincipal CurrentUser user) {
        return portfolioPositionService.findPortfolioPositionsView(user.getUserId());
    }
}
