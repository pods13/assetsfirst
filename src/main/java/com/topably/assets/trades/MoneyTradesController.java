package com.topably.assets.trades;

import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddMoneyTradeDto;
import com.topably.assets.trades.service.category.MoneyTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/money/trades")
@RequiredArgsConstructor
public class MoneyTradesController {

    private final MoneyTradeService tradeService;

    @PostMapping
    public TradeDto addTrade(@Validated @RequestBody AddMoneyTradeDto dto, Principal principal) {
        return tradeService.addTrade(dto, principal.getName());
    }

}
