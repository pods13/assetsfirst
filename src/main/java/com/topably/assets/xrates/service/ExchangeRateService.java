package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

public interface ExchangeRateService {

    Collection<ExchangeRate> addExchangeRates(Collection<ExchangeRate> rates);

    Collection<ExchangeRate> updateExchangeRates(Currency destinationCurrency, Collection<ExchangeRate> rates);

    Optional<ExchangeRate> findExchangeRate(Currency from, Currency to);
}
