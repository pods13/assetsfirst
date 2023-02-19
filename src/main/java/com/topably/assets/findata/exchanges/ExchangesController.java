package com.topably.assets.findata.exchanges;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangesController {

    private final ExchangeService exchangeService;

    @GetMapping("/tickers")
    public Page<Ticker> getTickers(Pageable pageable, @RequestParam(required = false) Set<String> instrumentTypes,
                                   @RequestParam(required = false) boolean inAnyPortfolio) {
        return exchangeService.getSymbols(pageable, instrumentTypes, inAnyPortfolio);
    }

    @GetMapping("/{exchange}/tickers")
    public Page<Ticker> getTickersByExchange(@PathVariable String exchange, Pageable pageable,
                                             @RequestParam(required = false) Set<String> instrumentTypes,
                                             @RequestParam(required = false) boolean inAnyPortfolio) {
        return exchangeService.getSymbolsByExchange(exchange, pageable, instrumentTypes, inAnyPortfolio);
    }
}
