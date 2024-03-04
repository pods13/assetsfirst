package com.topably.assets.trades.service.instrument;

import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.trades.domain.dto.manage.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.manage.EditTradeDto;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import com.topably.assets.trades.service.manage.TradeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FXTradeService implements InstrumentTradeService {

    private final FXRepository fxRepository;
    private final TradeManagementService tradeManagementService;

    @Override
    public TradeDto addTrade(AddTradeDto dto) {
        return tradeManagementService.addTrade(dto, fxRepository.getReferenceById(dto.getInstrumentId()));
    }

    @Override
    public TradeDto editTrade(EditTradeDto dto) {
        return tradeManagementService.editTrade(dto, fxRepository.getReferenceById(dto.getInstrumentId()));

    }

    @Override
    public void deleteTrade(DeleteTradeDto dto) {
        tradeManagementService.deleteTrade(dto, fxRepository.getReferenceById(dto.getInstrumentId()));
    }
}
