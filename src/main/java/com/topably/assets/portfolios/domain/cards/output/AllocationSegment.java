package com.topably.assets.portfolios.domain.cards.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AllocationSegment {

    private String name;
    private BigDecimal value;
    private Collection<AllocationSegment> children;
}
