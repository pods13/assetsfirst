package com.topably.assets.exchanges;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangesController {

    private final ExchangeService exchangeService;

    @GetMapping("/tickers")
    public Page<TickerSymbol> getTickers(Pageable pageable, @RequestParam(required = false) Set<String> instrumentTypes) {
        return exchangeService.getTickers(pageable, instrumentTypes);
    }

    @GetMapping("/{exchange}/tickers")
    public Page<TickerSymbol> getTickersByExchange(@PathVariable String exchange, Pageable pageable,
                                                   @RequestParam(required = false) Set<String> instrumentTypes) {
        return exchangeService.getTickersByExchange(exchange, pageable, instrumentTypes);
    }
}
