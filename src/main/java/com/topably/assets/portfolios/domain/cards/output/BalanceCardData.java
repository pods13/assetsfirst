package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceCardData implements CardData {

    private BigDecimal currentAmount;
    private BigDecimal investedAmount;
    private String currencySymbol;
}
