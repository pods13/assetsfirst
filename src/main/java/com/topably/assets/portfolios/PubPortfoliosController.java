package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.PubPortfolioDividendDto;
import com.topably.assets.portfolios.domain.dto.pub.PubPortfolioInfoDto;
import com.topably.assets.portfolios.service.PortfolioDividendService;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/portfolios")
@RequiredArgsConstructor
public class PubPortfoliosController {

    private final PortfolioService portfolioService;
    private final PortfolioDividendService portfolioDividendService;

    @GetMapping("/{identifier}")
    public PubPortfolioInfoDto getPortfolioInfoByIdentifier(@AuthenticationPrincipal CurrentUser user, @PathVariable String identifier) {
        return portfolioService.getPortfolioInfo(user, identifier);
    }

    @GetMapping("/{identifier}/dividends")
    public Page<PubPortfolioDividendDto> findUpcomingDividends(
        @AuthenticationPrincipal CurrentUser user,
        @PathVariable String identifier, @ParameterObject Pageable pageable
    ) {
        return portfolioDividendService.findUpcomingDividends(user, identifier, pageable);
    }

}
