package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.AggregatedTrade;
import com.topably.assets.trades.domain.Trade;

import java.util.Collection;

public interface TradeService {

    Collection<Trade> findUserTrades(String username);

    Collection<Trade> findUserDividendPayingTrades(String username);

    Collection<AggregatedTrade> findUserAggregatedTrades(String username);

    Collection<AggregatedTrade> findUserAggregatedStockTrades(String username);

    TradeDto addTrade(AddTradeDto dto, String username, Instrument tradedInstrument);

    Collection<TradeView> getUserTrades(String username);
}
