package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class BalanceCardData implements CardData {

    private BigDecimal currentAmount;
    private BigDecimal investedAmount;
    private String currencySymbol;
    private TimeFrameSummary investedAmountByDates;

    public record TimeFrameSummary(List<String> xaxis, List<BigDecimal> values) {

    }
}
