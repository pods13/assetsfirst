package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.dto.PortfolioDividendDto;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.service.PortfolioDividendService;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/portfolios")
@RequiredArgsConstructor
public class PublicPortfoliosController {

    private final PortfolioService portfolioService;
    private final PortfolioDividendService portfolioDividendService;

    @GetMapping("/{identifier}")
    public PortfolioDto getPortfolioInfo(@PathVariable String identifier) {
        return portfolioService.getPortfolioInfo(identifier);
    }

    @GetMapping("/{identifier}/dividends")
    public Page<PortfolioDividendDto> findUpcomingDividends(@PathVariable String identifier, Pageable pageable) {
        return portfolioDividendService.findUpcomingDividends(identifier, pageable);
    }
}
