package com.topably.assets.xrates.service.provider;

import com.topably.assets.xrates.domain.ExchangeRate;

import java.time.Instant;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

public interface ExchangeProvider {

    Collection<ExchangeRate> getExchangeRates(Instant time);

    Collection<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain);

}
