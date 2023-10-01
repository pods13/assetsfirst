package com.topably.assets.findata.xrates.domain.fc;

import java.math.BigDecimal;
import java.util.Map;

public record CurrencyHistoricalExchangeRateData(Map<String, Map<String, BigDecimal>> data) {
}
