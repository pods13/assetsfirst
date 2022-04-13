package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationCardData implements PortfolioCardData {

    private List<AllocationSegment> segments;
    private BigDecimal totalInvested;

}
