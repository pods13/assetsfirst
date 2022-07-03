package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface ExchangeService {

    Page<TickerSymbol> getTickers(Pageable pageable, Set<String> instrumentTypes);

    Page<TickerSymbol> getTickersByExchange(String exchange, Pageable pageable, Set<String> instrumentTypes);

    Optional<BigDecimal> findTickerRecentPrice(TickerSymbol symbol);
}
