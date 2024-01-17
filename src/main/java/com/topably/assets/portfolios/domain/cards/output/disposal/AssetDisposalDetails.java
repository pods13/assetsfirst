package com.topably.assets.portfolios.domain.cards.output.disposal;

import java.math.BigDecimal;

import com.topably.assets.core.domain.Ticker;


public record AssetDisposalDetails(Ticker ticker, BigDecimal total) {
}
