package com.topably.assets.trades;

import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddStockTradeDto;
import com.topably.assets.trades.service.category.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
    public TradeDto addTrade(@Validated @RequestBody AddStockTradeDto dto, Principal principal) {
        return tradeService.addTrade(dto, principal.getName());
    }
}
