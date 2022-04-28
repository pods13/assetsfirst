package com.topably.assets.securities.service;

import com.topably.assets.securities.domain.Stock;
import com.topably.assets.securities.domain.dto.StockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface StockService {

    Page<StockDto> findAll(Pageable pageable);

    Collection<Stock> findAllById(Collection<Long> ids);
}
