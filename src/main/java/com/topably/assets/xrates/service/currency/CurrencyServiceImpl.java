package com.topably.assets.xrates.service.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Currency;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    @Override
    public Collection<Currency> getAvailableCurrencies() {
        return Set.of(Currency.getInstance("RUB"), Currency.getInstance("USD"), Currency.getInstance("EUR"),
                Currency.getInstance("GBP"), Currency.getInstance("CNY"), Currency.getInstance("HKD"));
    }

    @Override
    public Collection<String> getAvailableCurrencyCodes() {
        return getAvailableCurrencies().stream().map(Currency::getCurrencyCode).collect(toSet());
    }
}
