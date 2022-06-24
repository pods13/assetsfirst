package com.topably.assets.portfolios.domain.cards.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.interning.qual.CompareToMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SectoralDistributionDataItem implements Comparable<SectoralDistributionDataItem> {

    private String name;
    private BigDecimal value;
    private Collection<SectoralDistributionDataItem> children;

    @Override
    public int compareTo(SectoralDistributionDataItem o) {
        return o.getValue().compareTo(this.getValue());
    }
}
