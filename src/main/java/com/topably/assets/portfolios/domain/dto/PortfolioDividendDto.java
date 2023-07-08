package com.topably.assets.portfolios.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Data
@Accessors(chain = true)
public class PortfolioDividendDto {
    private String name;
    private LocalDate recordDate;
    private BigDecimal perShare;
    private BigDecimal total;
    private Currency currency;
}
