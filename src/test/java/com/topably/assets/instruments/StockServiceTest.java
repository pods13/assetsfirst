package com.topably.assets.instruments;

import com.topably.assets.instruments.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSE;
import static org.assertj.core.api.Assertions.assertThat;


@IT
public class StockServiceTest extends IntegrationTestBase {

    @Autowired
    private StockService stockService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void givenStockData_whenStockWasNotPreviouslyImported_thenStockInstrumentIsCreated() {
        stockService.importStock(StockDataDto.builder()
            .identifier(new Ticker("TEST", NYSE.name()))
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("Газ и нефть")
                    .sector("Энергетика")
                    .build())
            .build());
    }

    @Test
    public void givenStockDataWithoutSector_whenStockImportedAgain_thenStockInstrumentSectorIsAdded() {
        var ticker = new Ticker("TEST", NYSE.name());
        stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("Газ и нефть")
                    .build())
            .build());
        entityManager.flush();
        entityManager.clear();

        stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("Газ и нефть")
                    .sector("Энергетика")
                    .build())
            .build());
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument.getTags()).hasSize(2);
    }

    @Test
    public void givenStockDataWithUnknownSector_whenStockImported_thenSuccessfullyImported() {
        var ticker = new Ticker("TEST", NYSE.name());
        stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .sector("Unknown")
                    .build())
            .build());
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getTags()).hasSize(0);
    }

    @Test
    public void givenStockDataWithUnknownIndustry_whenStockImported_thenSuccessfullyImported() {
        var ticker = new Ticker("TEST", NYSE.name());
        stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("Unknown")
                    .build())
            .build());
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getTags()).hasSize(0);
    }

}
