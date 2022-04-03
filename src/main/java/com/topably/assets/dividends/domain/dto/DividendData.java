package com.topably.assets.dividends.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class DividendData {

    private LocalDate declareDate;

    private LocalDate recordDate;

    private LocalDate payDate;

    private BigDecimal amount;
}
