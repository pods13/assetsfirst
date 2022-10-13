package com.topably.assets.portfolios.domain.cards.input.allocation;

import com.topably.assets.core.domain.Ticker;
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
public class AllocationAggregatedTrade {

    private Ticker identifier;
    private Long instrumentId;
    private String instrumentType;
    private BigInteger quantity;
    private BigDecimal price;
    private Currency currency;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity()));
    }
}
