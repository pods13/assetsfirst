package com.topably.assets.findata.xrates.service.provider;

import com.topably.assets.findata.xrates.domain.ExchangeRate;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

public interface ExchangeProvider {

    default List<ExchangeRate> getExchangeRates(Instant time) {
        return getExchangeRates(time, Collections.emptySet());
    }

    List<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain);

}
