package com.topably.assets.trades.service.instrument;

import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FXTradeService implements InstrumentTradeService {

    private final FXRepository fxRepository;
    private final TradeService tradeService;

    @Override
    public TradeDto addTrade(AddTradeDto dto) {
        return tradeService.addTrade(dto, fxRepository.getById(dto.getInstrumentId()));
    }

    @Override
    public TradeDto editTrade(EditTradeDto dto) {
        return tradeService.editTrade(dto, fxRepository.getById(dto.getInstrumentId()));

    }
}
