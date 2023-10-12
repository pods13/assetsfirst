package com.topably.assets.portfolios.domain.cards.output.dividend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@Accessors(chain = true)
public class DividendSummary {

    private String name;
    private String stack;
    private BigDecimal value;
    private Collection<DividendDetails> details;
    private String currencyCode;
}
