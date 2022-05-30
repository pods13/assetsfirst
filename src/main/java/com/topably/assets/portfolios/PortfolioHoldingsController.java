package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/portfolio-holdings")
@RequiredArgsConstructor
public class PortfolioHoldingsController {

    private final PortfolioHoldingService portfolioHoldingService;

    @GetMapping
    public Collection<PortfolioHoldingDto> getPortfolioHoldings(Principal principal) {
        return portfolioHoldingService.findPortfolioHoldings(principal.getName());
    }
}
