package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.Trade;

import java.util.Collection;

public interface TradeService {

    Collection<Trade> findDividendPayingTrades(Long portfolioId);

    TradeDto addTrade(AddTradeDto dto, Instrument tradedInstrument);

    Collection<TradeView> getUserTrades(String username);
}
