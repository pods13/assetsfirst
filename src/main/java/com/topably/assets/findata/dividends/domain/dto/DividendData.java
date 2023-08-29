package com.topably.assets.findata.dividends.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DividendData {

    private LocalDate declareDate;

    private LocalDate recordDate;

    private LocalDate payDate;

    private BigDecimal amount;
}
