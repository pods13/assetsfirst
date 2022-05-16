package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.Stock;
import com.topably.assets.instruments.domain.dto.StockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface StockService {

    Page<StockDto> findAll(Pageable pageable);

    Collection<Stock> findAllById(Collection<Long> ids);
}
