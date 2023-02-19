package com.topably.assets.findata.xrates;

import com.topably.assets.findata.xrates.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrenciesController {

    private final CurrencyService currencyService;

    @GetMapping("/codes")
    public Collection<String> getAvailableCurrencyCodes() {
        return currencyService.getAvailableCurrencyCodes();
    }
}
