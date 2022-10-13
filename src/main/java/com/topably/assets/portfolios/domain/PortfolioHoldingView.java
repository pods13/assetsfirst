package com.topably.assets.portfolios.domain;

import com.topably.assets.core.domain.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHoldingView {

    private Long id;
    private Long instrumentId;
    private String instrumentType;
    private Ticker identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private String currencySymbol;
    private BigDecimal pctOfPortfolio;
    private BigDecimal marketValue;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity()));
    }
}
