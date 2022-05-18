package com.topably.assets.trades.service.instrument;

import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;

public interface InstrumentTradeService {

    TradeDto addTrade(AddTradeDto dto, String username);
}
