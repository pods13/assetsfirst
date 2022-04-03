package com.topably.assets.xrates.service.provider;

import com.topably.assets.xrates.domain.ExchangeRate;

import java.time.Instant;
import java.util.Currency;
import java.util.List;

public interface ExchangeProvider {

    List<ExchangeRate> getExchangeRates(Instant time);

}
