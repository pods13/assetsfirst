package com.topably.assets.fundamentals.domain;

import com.topably.assets.core.domain.TickerSymbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundamentalsDto {

    private TickerSymbol identifier;
    private BigDecimal marketValue;
    private BigDecimal convertedMarketValue;
}
