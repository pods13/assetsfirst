package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfoliosController {

    private final PortfolioService portfolioService;

    @GetMapping("/{identifier}")
    public PortfolioDto getPortfolioInfo(@PathVariable String identifier) {
        return portfolioService.getPortfolioInfo(identifier);
    }
}
