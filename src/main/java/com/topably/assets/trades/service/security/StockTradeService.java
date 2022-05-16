package com.topably.assets.trades.service.security;

import com.topably.assets.instruments.repository.security.StockRepository;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.SecurityTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockTradeService implements FinInstTradeService {

    private final StockRepository stockRepository;
    private final SecurityTradeService securityTradeService;

    @Override
    public TradeDto addTrade(AddTradeDto dto, String username) {
        return securityTradeService.addTrade(dto, username, stockRepository.getById(dto.getInstrumentId()));
    }
}
