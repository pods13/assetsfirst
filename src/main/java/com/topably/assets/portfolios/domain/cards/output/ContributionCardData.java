package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Collection;

@Data
@Accessors(chain = true)
public class ContributionCardData implements CardData {

    private Collection<String> xaxis;
    private Collection<Contribution> contributions;
    private BigDecimal totalContributed;
    private String currencyCode;

    public record Contribution(String name, Collection<BigDecimal> data) {
    }

}
