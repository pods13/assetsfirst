package com.topably.assets.portfolios.domain.cards.output.dividend.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PositionItem {

    private String name;
    private BigDecimal averagePrice;
    private BigDecimal annualDividend;
    private BigDecimal currentYield;

    private Collection<BigDecimal> targets;

}
