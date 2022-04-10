package com.topably.assets.portfolios.domain.cards.output.dividend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class DividendSummary {

    private String name;
    private BigDecimal value;
}
