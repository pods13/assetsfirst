package com.topably.assets.instruments;

import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.USExchange;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IT
public class StockServiceTest extends IntegrationTestBase {

    @Autowired
    private StockService stockService;

    @Test
    public void givenStockData_whenStockWasNotPreviouslyImported_thenStockInstrumentIsCreated() {
        stockService.importStock(StockDataDto.builder()
            .identifier(new Ticker("TEST", USExchange.NYSE.name()))
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("TEST Industry")
                    .sector("TEST Sector")
                    .build())
            .build());
    }
}
