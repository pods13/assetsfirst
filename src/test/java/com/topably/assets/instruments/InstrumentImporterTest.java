package com.topably.assets.instruments;

import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.instruments.service.importer.DefaultInstrumentImporter;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Currency;

import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.MCX;
import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSE;
import static org.assertj.core.api.Assertions.assertThat;


@IT
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InstrumentImporterTest extends IntegrationTestBase {

    @Autowired
    private DefaultInstrumentImporter importer;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void givenStockData_whenStockWasNotPreviouslyImported_thenStockInstrumentIsCreated() {
        var dto = new ImportInstrumentDto()
            .setIdentifier(new Ticker("TEST", NYSE.name()))
            .setName("Test Company")
            .setSector("Энергетика")
            .setIndustry("Газ и нефть")
            .setType(InstrumentType.STOCK);
        importer.importInstrument(dto);
    }

    @Test
    public void givenStockDataWithoutSector_whenStockImportedAgain_thenStockInstrumentSectorIsAdded() {
        var ticker = new Ticker("TEST", NYSE.name());
        importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setIndustry("Газ и нефть")
            .setType(InstrumentType.STOCK));
        entityManager.flush();
        entityManager.clear();

        importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setIndustry("Газ и нефть")
            .setSector("Энергетика")
            .setType(InstrumentType.STOCK));
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument.getTags()).hasSize(2);
    }

    @Test
    public void givenStockDataWithUnknownSector_whenStockImported_thenSuccessfullyImported() {
        var ticker = new Ticker("TEST", NYSE.name());
        var dto = new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setSector("Unknown")
            .setType(InstrumentType.STOCK);
        importer.importInstrument(dto);
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getTags()).hasSize(0);
    }

    @Test
    public void givenStockData_whenTheSameDataForStockImportedAgain_thenImportSuccessful() {
        var ticker = new Ticker("TEST", NYSE.name());
        var companyName = "Test Company";
        importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName(companyName)
            .setIndustry("Газ и нефть")
            .setType(InstrumentType.STOCK));
        entityManager.flush();
        entityManager.clear();

        importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(new Ticker("TEST", NYSE.name()))
            .setName(companyName)
            .setIndustry("Газ и нефть")
            .setSector("Энергетика")
            .setType(InstrumentType.STOCK));
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getName()).isEqualTo(companyName);
        assertThat(instrument.getTags()).hasSize(2);
    }

    @Test
    public void givenStockDataWithUnknownIndustry_whenStockImported_thenSuccessfullyImported() {
        var ticker = new Ticker("TEST", NYSE.name());
        var dto = new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setIndustry("Unknown")
            .setType(InstrumentType.STOCK);
        importer.importInstrument(dto);
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getTags()).hasSize(0);
    }

    @Test
    public void givenStockData_whenStockImported_thenSuccessfullyImportedWithFilledFields() {
        var ticker = new Ticker("TEST", NYSE.name());
        var dto = new ImportInstrumentDto()
                .setIdentifier(ticker)
                .setName("Test Company")
                .setIndustry("Unknown")
                .setType(InstrumentType.STOCK);
        importer.importInstrument(dto);
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getAttributes()).isEmpty();
        assertThat(instrument.getCurrency()).isEqualTo(NYSE.getCurrency());
        assertThat(instrument.getInstrumentType()).isEqualTo(InstrumentType.STOCK.name());
    }


    @Test
    public void givenETFData_whenEtfImportedWithSpecifiedCurrency_thenSuccessfullyImported() {
        Ticker ticker = new Ticker("TEST.ETF", MCX.name());
        var dto = new ImportInstrumentDto()
                .setIdentifier(ticker)
                .setName("Test ETF")
                .setIndustry("Unknown")
                .setType(InstrumentType.ETF)
                .setCurrency(Currency.getInstance("USD"));
        importer.importInstrument(dto);
        entityManager.flush();
        entityManager.clear();

        var instrument = instrumentService.findInstrument(ticker.getSymbol(), ticker.getExchange());
        assertThat(instrument).isNotNull();
        assertThat(instrument.getCurrency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(instrument.getInstrumentType()).isEqualTo(InstrumentType.ETF.name());
    }

}
