package com.topably.assets.dividends.domain.dto;

import com.topably.assets.core.domain.Ticker;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Data
@Accessors(chain = true)
public class AggregatedDividendDto {

    private Ticker ticker;
    private LocalDate payDate;
    private boolean forecasted;
    private BigDecimal total;
    private Currency currency;
}
