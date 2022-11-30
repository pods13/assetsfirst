package com.topably.assets.portfolios.domain.cards.output.dividend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class DividendSummary {

    private String name;
    private BigDecimal value;
    private Collection<DividendDetails> details;
}
