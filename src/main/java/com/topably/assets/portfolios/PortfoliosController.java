package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.PortfolioShortInfoDto;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
public class PortfoliosController {

    private final PortfolioService portfolioService;

    @GetMapping("/me")
    public PortfolioShortInfoDto getUserPortfolioInfo(@AuthenticationPrincipal CurrentUser user) {
        return portfolioService.getPortfolioShortInfoByUser(user);
    }

}
