package com.topably.assets.trades.domain.dto;

import com.topably.assets.core.domain.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AggregatedTradeDto {

    private BigInteger quantity;
    private BigDecimal price;
    private InterimTradeResult interimTradeResult;


    public record InterimTradeResult(Collection<TradeData> buyTradesData, BigDecimal closedPnl) {
    }

    public record TradeData(BigInteger shares, BigDecimal price, LocalDate tradeTime) {
    }
}
