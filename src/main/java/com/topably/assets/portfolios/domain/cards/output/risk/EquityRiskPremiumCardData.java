package com.topably.assets.portfolios.domain.cards.output.risk;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class EquityRiskPremiumCardData implements CardData {

    private List<EquityData> equities;

    public record EquityData(String name, BigDecimal riskPremium) {
    }
}
