package com.topably.assets.portfolios.domain;

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
public class PortfolioHoldingView {

    private Long id;
    private Long instrumentId;
    private String instrumentType;
    private TickerSymbol identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private String currencySymbol;
    private BigDecimal pctOfPortfolio;
    private BigDecimal marketValue;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity()));
    }
}
