package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerSymbol;

import java.util.Collection;

public interface ExchangeService {

    Collection<TickerSymbol> findTickersByExchange(String exchange);
}
