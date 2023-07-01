package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.dto.PortfolioValuesByDates;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class BalanceCardData implements CardData {

    private BigDecimal currentAmount;
    private BigDecimal investedAmount;
    private String currencySymbol;
    private PortfolioValuesByDates investedAmountByDates;
}
