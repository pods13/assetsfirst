package com.topably.assets.portfolios.domain.cards.input.allocation;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AllocationCard extends DashboardCard {

    private AllocatedByOption allocatedBy;
    private List<CustomSegment> customSegments;

}
