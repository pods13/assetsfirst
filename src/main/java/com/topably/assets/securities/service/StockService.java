package com.topably.assets.securities.service;

import com.topably.assets.securities.domain.dto.StockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockService {

    Page<StockDto> findAll(Pageable pageable);
}
