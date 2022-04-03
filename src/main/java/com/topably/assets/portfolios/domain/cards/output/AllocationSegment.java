package com.topably.assets.portfolios.domain.cards.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AllocationSegment {

    private String name;
    private BigDecimal value;
}
