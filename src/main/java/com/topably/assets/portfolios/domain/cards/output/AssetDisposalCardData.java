package com.topably.assets.portfolios.domain.cards.output;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collection;

@Data
@Accessors(chain = true)
public class AssetDisposalCardData implements CardData {

    private Collection<BigDecimal> losses;
    private Collection<BigDecimal> profits;
    private Collection<BigDecimal> taxableIncome;
    private Collection<Year> trackedYears;
    private String currencyCode;
}
