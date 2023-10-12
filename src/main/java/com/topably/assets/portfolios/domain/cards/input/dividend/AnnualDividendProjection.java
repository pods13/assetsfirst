package com.topably.assets.portfolios.domain.cards.input.dividend;

import java.math.BigDecimal;
import java.util.Currency;

public record AnnualDividendProjection(String ticker, BigDecimal dividend, Currency currency) {
}
