package com.topably.assets.findata.xrates.service.provider;

import com.topably.assets.findata.xrates.domain.ExchangeRate;

import java.time.Instant;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

public interface ExchangeProvider {

    List<ExchangeRate> getExchangeRates(Instant time);

    List<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain);

}
