package com.topably.assets.portfolios.domain.cards.output.dividend;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Getter
@AllArgsConstructor
public class DividendDetails {

    private String name;
    private LocalDate payDate;
    private boolean forecasted;
    private BigDecimal total;
    private Currency currency;
}
