package com.topably.assets.instruments;

import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StockService stockService;

    @GetMapping
    public Page<StockDto> findStocks(Pageable pageable) {
        return stockService.findAll(pageable);
    }

    @PostMapping("/import")
    public StockDto importStock(@Valid @RequestBody StockDataDto stockToImport) {
        return stockService.importStock(stockToImport);
    }
}
