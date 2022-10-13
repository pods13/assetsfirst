package com.topably.assets.portfolios.domain.dto;

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
public class PortfolioHoldingDto {

    private Long id;
    private Long instrumentId;
    private String instrumentType;
    private Ticker identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private Currency currency;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity()));
    }
}
