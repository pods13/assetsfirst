package com.topably.assets.portfolios.domain.cards.input.allocation;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AllocationCard extends DashboardCard {

    private AllocatedByOption allocatedBy;

}
