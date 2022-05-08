package com.topably.assets.xrates.service.currency;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final ExchangeRateService exchangeRateService;

    @Override
    public Collection<Currency> getAvailableCurrencies() {
        return Set.of(Currency.getInstance("RUB"), Currency.getInstance("USD"), Currency.getInstance("EUR"));
    }

    @Override
    public Collection<String> getAvailableCurrencyCodes() {
        return getAvailableCurrencies().stream().map(Currency::getCurrencyCode).collect(toSet());
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from) {
        return convert(amount, from, DESTINATION_CURRENCY);
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from.equals(to)) {
            return amount;
        }
        Optional<ExchangeRate> exchangeRate = exchangeRateService.findExchangeRate(from, to);
        return exchangeRate
                .map(ExchangeRate::getConversionRate)
                .map(amount::multiply)
                .orElseThrow();
    }
}
