package com.topably.assets.portfolios.domain.cards.input;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameOption;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DividendIncomeCard extends DashboardCard {

    private TimeFrameOption timeFrame;
}
