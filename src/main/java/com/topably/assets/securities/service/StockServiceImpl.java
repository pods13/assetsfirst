package com.topably.assets.securities.service;

import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.securities.domain.dto.StockDto;
import com.topably.assets.securities.repository.security.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
