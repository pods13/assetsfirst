package com.topably.assets.xrates.service.currency;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyConverterServiceImpl implements CurrencyConverterService {
    //TODO use currency selected for user portfolio instead
    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final ExchangeRateService exchangeRateService;

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from) {
        return convert(amount, from, DESTINATION_CURRENCY, LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to, Instant time) {
        if (from.equals(to)) {
            return amount;
        }
        Optional<ExchangeRate> exchangeRate = exchangeRateService.findExchangeRate(from, to, time);
        return exchangeRate
            .map(ExchangeRate::getConversionRate)
            .map(amount::multiply)
            .orElseThrow();
    }
}
