package com.topably.assets.trades.domain;

import com.topably.assets.exchanges.domain.TickerSymbol;
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
public class AggregatedTrade {

    private Long instrumentId;
    private TickerSymbol identifier;
    private BigInteger quantity;
    private BigDecimal total;
    private Currency currency;
}
