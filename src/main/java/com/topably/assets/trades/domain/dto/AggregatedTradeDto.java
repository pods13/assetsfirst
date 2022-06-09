package com.topably.assets.trades.domain.dto;

import com.topably.assets.core.domain.TickerSymbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedTradeDto {

    private TickerSymbol identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private Currency currency;
}
