package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.dto.AddPortfolioDto;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfoliosController {

    private final PortfolioService portfolioService;

    @GetMapping
    public Collection<PortfolioDto> getUserPortfolios(Principal principal) {
        return this.portfolioService.findUserPortfolios(principal.getName());
    }

    @PostMapping
    public PortfolioDto addPortfolio(Principal principal, AddPortfolioDto dto) {
        return this.portfolioService.addPortfolio(principal.getName(), dto);
    }
}
