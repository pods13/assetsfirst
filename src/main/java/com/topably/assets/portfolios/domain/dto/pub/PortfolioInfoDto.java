package com.topably.assets.portfolios.domain.dto.pub;

import com.topably.assets.portfolios.domain.dto.PortfolioValuesByDates;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PortfolioInfoDto {

    private BigDecimal valueIncreasePct;
    private String currencyCode;
    private PortfolioValuesByDates investedValueByDates;
    private PortfolioValuesByDates marketValueByDates;
}
