package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

public interface ExchangeService {

    Page<TickerSymbol> findTickersByExchange(String exchange, Pageable pageable);

    Optional<BigDecimal> findTickerRecentPrice(TickerSymbol symbol);
}
