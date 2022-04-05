package com.topably.assets.exchanges;

import com.topably.assets.exchanges.domain.TickerDto;
import com.topably.assets.exchanges.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangesController {

    private final ExchangeService exchangeService;

    @GetMapping("/:exchange/tickers")
    public Collection<TickerDto> findTickers(@PathVariable String exchange) {
        return exchangeService.findTickersByExchange(exchange);
    }
}
