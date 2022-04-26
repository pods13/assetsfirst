package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectoralDistributionCardData implements PortfolioCardData {

    private String test;
}
