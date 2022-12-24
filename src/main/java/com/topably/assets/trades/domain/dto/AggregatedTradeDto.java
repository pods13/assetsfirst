package com.topably.assets.trades.domain.dto;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.trades.domain.TradeView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AggregatedTradeDto {

    private BigInteger quantity;
    private BigDecimal price;
    private BigDecimal closedPnl;
    private Collection<TradeData> buyTradesData;

    public record InterimTradeResult(Collection<TradeData> buyTradesData, BigDecimal closedPnl) {
    }

    @Data
    @Accessors(chain = true)
    public static final class TradeData {
        private BigInteger shares;
        private BigDecimal price;
        private LocalDate tradeTime;
        private Currency currency;
        private String brokerName;
        private Long instrumentId;
        private String instrumentType;
        private Ticker ticker;

        public TradeData(BigInteger shares, BigDecimal price, LocalDate tradeTime, TradeView trade) {
            this.shares = shares;
            this.price = price;
            this.tradeTime = tradeTime;
            this.currency = trade.getCurrency();
            this.brokerName = trade.getBrokerName();
            this.instrumentId = trade.getInstrumentId();
            this.instrumentType = trade.getInstrumentType();
            this.ticker = new Ticker(trade.getSymbol(), trade.getExchange());
        }
    }
}
