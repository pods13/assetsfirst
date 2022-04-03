package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

public interface ExchangeRateService {

    Collection<ExchangeRate> addExchangeRates(Collection<ExchangeRate> rates);

    Collection<ExchangeRate> updateExchangeRates(Currency destinationCurrency, Collection<ExchangeRate> rates);

    BigDecimal convertCurrency(BigDecimal amount, Currency from, Currency to);
}
