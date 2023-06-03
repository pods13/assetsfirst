package com.topably.assets.portfolios.domain.position;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.portfolios.domain.dto.tag.TagProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioPositionView {

    private Long id;
    private Long instrumentId;
    private String instrumentType;
    private Ticker identifier;
    private BigInteger quantity;
    private BigDecimal price;
    private String currencySymbol;
    private BigDecimal pctOfPortfolio;
    private BigDecimal marketValue;
    private BigDecimal yieldOnCost;
    private List<TagProjection> tags;
    private BigDecimal accumulatedDividends;

    public BigDecimal getTotal() {
        return getPrice().multiply(new BigDecimal(getQuantity()));
    }
}
