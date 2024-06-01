package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface StockService {

    Page<StockDto> findAll(Pageable pageable);

    StockDto addStock(StockDataDto dto);

    StockDto importStock(StockDataDto dto);
}
