package com.topably.assets.portfolios.domain.cards.output.dividend.goal;

import com.topably.assets.exchanges.domain.TickerSymbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PositionItem {

    private String name;
    private BigDecimal averagePrice;
    private BigDecimal annualDividend;
    private BigDecimal currentYield;

}
