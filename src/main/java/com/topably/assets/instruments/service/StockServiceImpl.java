package com.topably.assets.instruments.service;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public Page<StockDto> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
                .map(stock -> StockDto.builder()
                        .id(stock.getId())
                        .identifier(new TickerSymbol(stock.getTicker(), stock.getExchange().getCode()))
                        .companyId(stock.getCompanyId())
                        .build());
    }

    @Override
    public Collection<Stock> findAllById(Collection<Long> ids) {
        return stockRepository.findAllById(ids);
    }
}
