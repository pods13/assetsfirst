package com.topably.assets.xrates.service.currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

public interface CurrencyConverterService {

    BigDecimal convert(BigDecimal amount, Currency from);

    BigDecimal convert(BigDecimal amount, Currency from, Currency to, Instant time);
}
