package com.topably.assets.trades.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.Trade;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

public interface TradeService {

    Collection<Trade> findDividendPayingTrades(Long portfolioId);

    TradeDto addTrade(AddTradeDto dto, Instrument tradedInstrument);

    Collection<TradeView> getUserTrades(String username);

    Collection<Trade> findTradesByUserId(Long userId);

    TradeDto editTrade(EditTradeDto dto, Instrument tradedInstrument);

    BigDecimal calculateInvestedAmountByHoldingId(Long holdingId, Currency holdingCurrency, Currency portfolioCurrency);

    void deleteTrade(DeleteTradeDto dto, Instrument tradedInstrument);
}
