package com.topably.assets.fundamentals;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.fundamentals.domain.FundamentalsDto;
import com.topably.assets.fundamentals.service.FundamentalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/fundamentals")
@RequiredArgsConstructor
public class FundamentalsController {

    private final FundamentalsService fundamentalsService;

    @GetMapping
    public Collection<FundamentalsDto> findPortfolioHoldingsFundamentals(@AuthenticationPrincipal CurrentUser user) {
        return fundamentalsService.findPortfolioHoldingsFundamentals(user.getUserId());
    }
}
