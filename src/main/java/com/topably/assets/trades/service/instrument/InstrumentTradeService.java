package com.topably.assets.trades.service.instrument;

import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;

public interface InstrumentTradeService {

    TradeDto addTrade(AddTradeDto dto);

    TradeDto editTrade(EditTradeDto dto);

    void deleteTrade(DeleteTradeDto dto);
}
