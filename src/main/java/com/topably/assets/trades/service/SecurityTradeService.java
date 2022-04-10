package com.topably.assets.trades.service;

import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.security.SecurityTrade;

import java.util.Collection;

public interface SecurityTradeService {

    Collection<SecurityTrade> findUserTrades(String username);

    Collection<SecurityTrade> findUserDividendPayingTrades(String username);

    TradeDto addTrade(AddTradeDto dto, String username, Security tradedSecurity);
}
