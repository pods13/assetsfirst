package com.topably.assets.portfolios.domain.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class PortfolioShortInfoDto {

    private BigDecimal investedValue;
    private String currencyCode;
}
