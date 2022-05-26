package com.topably.assets.trades.service.instrument;

import com.topably.assets.instruments.repository.instrument.StockRepository;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockTradeService implements InstrumentTradeService {

    private final StockRepository stockRepository;
    private final TradeService tradeService;

    @Override
    public TradeDto addTrade(AddTradeDto dto) {
        return tradeService.addTrade(dto, stockRepository.getById(dto.getInstrumentId()));
    }
}
