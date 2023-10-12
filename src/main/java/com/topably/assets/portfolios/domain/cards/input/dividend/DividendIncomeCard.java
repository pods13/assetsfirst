package com.topably.assets.portfolios.domain.cards.input.dividend;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameOption;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DividendIncomeCard extends DashboardCard {

    private TimeFrameOption timeFrame;

    private Boolean useCustomDividendProjections;
    private Map<String, BigDecimal> tickerByAnnualDividendProjection;
    private List<AnnualDividendProjection> annualDividendProjections;
}
