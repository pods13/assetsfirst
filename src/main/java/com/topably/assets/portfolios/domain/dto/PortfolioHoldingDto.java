package com.topably.assets.portfolios.domain.dto;

import com.topably.assets.core.domain.TickerSymbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHoldingDto {

    private Long instrumentId;
    private String instrumentType;
    private TickerSymbol identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private Currency currency;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity())).setScale(2, RoundingMode.HALF_UP);
    }
}
