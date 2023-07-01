package com.topably.assets.portfolios.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PortfolioDto {

    private BigDecimal valueIncreasePct;
    private String currencySymbol;
    private PortfolioValuesByDates investedAmountByDates;
}
