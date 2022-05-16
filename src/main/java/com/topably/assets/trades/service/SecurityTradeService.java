package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.security.SecurityAggregatedTrade;
import com.topably.assets.trades.domain.security.SecurityTrade;

import java.util.Collection;

public interface SecurityTradeService {

    Collection<SecurityTrade> findUserTrades(String username);

    Collection<SecurityTrade> findUserDividendPayingTrades(String username);

    Collection<SecurityAggregatedTrade> findUserAggregatedTrades(String username);

    Collection<SecurityAggregatedTrade> findUserAggregatedStockTrades(String username);

    TradeDto addTrade(AddTradeDto dto, String username, Instrument tradedInstrument);
}
