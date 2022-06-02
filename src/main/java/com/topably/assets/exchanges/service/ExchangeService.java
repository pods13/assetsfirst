package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public interface ExchangeService {

    Collection<TickerSymbol> findTickersByExchange(String exchange);

    Optional<BigDecimal> findTickerRecentPrice(TickerSymbol symbol);
}
