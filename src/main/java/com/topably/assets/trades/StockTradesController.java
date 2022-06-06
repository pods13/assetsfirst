package com.topably.assets.trades;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.instrument.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/stocks/trades")
@RequiredArgsConstructor
public class StockTradesController {

    private final StockTradeService tradeService;

    @PostMapping
    public TradeDto addTrade(@Validated @RequestBody AddTradeDto dto, @AuthenticationPrincipal CurrentUser user) {
        dto.setUserId(user.getUserId());
        return tradeService.addTrade(dto);
    }

    @PatchMapping
    public TradeDto editTrade(@Validated @RequestBody EditTradeDto dto, @AuthenticationPrincipal CurrentUser user) {
        dto.setUserId(user.getUserId());
        return this.tradeService.editTrade(dto);
    }
}
