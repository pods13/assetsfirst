package com.topably.assets.trades;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.service.TradeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradesController {

    private final TradeService tradeService;

    @GetMapping
    public Page<TradeView> getUserTrades(
        @Parameter(hidden = true) @AuthenticationPrincipal CurrentUser user,
        @ParameterObject @SortDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return tradeService.getUserTrades(user.getUserId(), pageable);
    }

}
