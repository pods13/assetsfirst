package com.topably.assets.findata.xrates.service.currency;

import com.topably.assets.findata.xrates.domain.ExchangeRate;
import com.topably.assets.findata.xrates.service.ExchangeRateService;
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
public class CurrencyConverterService {

    private final ExchangeRateService exchangeRateService;

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        return convert(amount, from, to, LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to, Instant time) {
        if (from.equals(to)) {
            return amount;
        }
        Optional<ExchangeRate> exchangeRate = exchangeRateService.findExchangeRate(from, to, time);
        return exchangeRate
            .map(ExchangeRate::getConversionRate)
            .map(amount::multiply)
            .orElseThrow(() -> new RuntimeException("Cannot find exchange rate " + from + "->" + to + " on date " + time));
    }
}
