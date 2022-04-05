package com.topably.assets.trades.service;

import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;

public interface SecurityTradeService {

    TradeDto addTrade(AddTradeDto dto, String username, Security tradedSecurity);
}
