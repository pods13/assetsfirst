package com.topably.assets.trades.service.instrument;

import com.topably.assets.instruments.repository.instrument.ETFRepository;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ETFTradeService implements InstrumentTradeService {

    private final ETFRepository etfRepository;
    private final TradeService tradeService;

    @Override
    @Transactional
    public TradeDto addTrade(AddTradeDto dto) {
        return tradeService.addTrade(dto, etfRepository.getById(dto.getInstrumentId()));
    }

    @Override
    @Transactional
    public TradeDto editTrade(EditTradeDto dto) {
        return tradeService.editTrade(dto, etfRepository.getById(dto.getInstrumentId()));
    }

    @Override
    @Transactional
    public void deleteTrade(DeleteTradeDto dto) {
        tradeService.deleteTrade(dto, etfRepository.getById(dto.getInstrumentId()));
    }
}
