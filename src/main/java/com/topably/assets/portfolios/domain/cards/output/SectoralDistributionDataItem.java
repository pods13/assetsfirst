package com.topably.assets.portfolios.domain.cards.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SectoralDistributionDataItem {

    private String name;
    private BigDecimal value;
    private Collection<SectoralDistributionDataItem> children;
}
