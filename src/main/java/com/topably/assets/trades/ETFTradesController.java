package com.topably.assets.trades;

import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.instrument.ETFTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/etfs/trades")
@RequiredArgsConstructor
public class ETFTradesController {

    private final ETFTradeService tradeService;

    @PostMapping
    public TradeDto addTrade(@Validated @RequestBody AddTradeDto dto, Principal principal) {
        dto.setUsername(principal.getName());
        return tradeService.addTrade(dto);
    }
}
