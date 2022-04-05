package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerDto;

import java.util.Collection;

public interface ExchangeService {

    Collection<TickerDto> findTickersByExchange(String exchange);
}
