package com.topably.assets.xrates.service.currency;

import java.util.Collection;
import java.util.Currency;

public interface CurrencyService {

    Collection<Currency> getAvailableCurrencies();

    Collection<String> getAvailableCurrencyCodes();
}
