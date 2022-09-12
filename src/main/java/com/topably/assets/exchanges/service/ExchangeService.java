package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface ExchangeService {

    Page<TickerSymbol> getSymbols(Pageable pageable, Set<String> instrumentTypes);

    Page<TickerSymbol> getSymbolsByExchange(String exchange, Pageable pageable, Set<String> instrumentTypes);

    Optional<BigDecimal> findSymbolRecentPrice(TickerSymbol symbol);
}
