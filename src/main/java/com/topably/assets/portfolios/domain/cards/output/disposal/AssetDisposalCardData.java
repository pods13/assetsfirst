package com.topably.assets.portfolios.domain.cards.output.disposal;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collection;
import java.util.List;


@Data
@Accessors(chain = true)
public class AssetDisposalCardData implements CardData {

    private BigDecimal losses;
    private Collection<AssetDisposalDetails> lossDetails;
    private BigDecimal profits;
    private Collection<AssetDisposalDetails> profitDetails;
    private BigDecimal taxableIncome;
    private Collection<Year> trackedYears;
    private String currencyCode;
}
