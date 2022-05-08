package com.topably.assets.xrates.service.currency;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

public interface CurrencyService {

    Collection<Currency> getAvailableCurrencies();

    Collection<String> getAvailableCurrencyCodes();

    BigDecimal convert(BigDecimal amount, Currency from);
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}
