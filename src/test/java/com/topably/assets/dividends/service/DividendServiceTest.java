package com.topably.assets.dividends.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.dividends.repository.DividendRepository;
import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.exchanges.domain.Exchange;
import com.topably.assets.findata.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IT
public class DividendServiceTest extends IntegrationTestBase {

    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private DividendService dividendService;

    @Test
    public void givenStockThatNeverPaidDividends_whenDividendAmountCalculated_thenReturnZeroAmount() {
        var ticker = new Ticker("TSLA", "NYSE");
        var instrument = createStockInstrument(ticker);

        var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2022));

        assertThat(actualAmount).isZero();
    }

    @Test
    public void givenOnlyOneYearDividends_whenDividendAmountCalculatedForFutureYear_thenReturnExistedYearAmount() {
        var ticker = new Ticker("ROSN", "MCX");
        var instrument = createStockInstrument(ticker);
        var expectedAmount = BigDecimal.TEN;
        addDividendData(List.of(new Dividend()
            .setAmount(expectedAmount)
            .setInstrument(instrument
            ).setRecordDate(LocalDate.of(2019, 5, 6))));

        var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2022));

        assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
    }

    @Test
    public void givenFirstTimeDividends_whenDividendAmountCalculatedForSameYear_thenReturnCorrectAmount() {
        var ticker = new Ticker("ROSN", "MCX");
        var instrument = createStockInstrument(ticker);
        var expectedAmount = BigDecimal.TEN;
        addDividendData(List.of(new Dividend()
            .setAmount(expectedAmount)
            .setInstrument(instrument
            ).setRecordDate(LocalDate.of(2021, 4, 23))));

        var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2021));

        assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
    }


    @Test
    public void givenThisYearDividendsNotFullyPaidAndDataForPrevYearPresented_whenDividendAmountCalculatedForSameYear_thenReturnCorrectAmount() {
        var ticker = new Ticker("ROSN", "MCX");
        var instrument = createStockInstrument(ticker);
        var expectedAmount = BigDecimal.valueOf(30L);
        addDividendData(List.of(new Dividend()
                .setAmount(BigDecimal.TEN)
                .setInstrument(instrument)
                .setRecordDate(LocalDate.of(2020, 4, 23)),
            new Dividend()
                .setAmount(BigDecimal.TEN)
                .setInstrument(instrument)
                .setRecordDate(LocalDate.of(2020, 2, 23)),
            new Dividend()
                .setAmount(BigDecimal.TEN)
                .setInstrument(instrument)
                .setRecordDate(LocalDate.of(2019, 8, 21)),
            new Dividend()
                .setAmount(BigDecimal.TEN)
                .setInstrument(instrument)
                .setRecordDate(LocalDate.of(2019, 4, 23))));

        var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2020));

        assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
    }

    private Collection<Dividend> addDividendData(Collection<Dividend> data) {
        return dividendRepository.saveAll(data);
    }

    private Instrument createStockInstrument(Ticker ticker) {
        var exchange = exchangeRepository.save(Exchange.builder()
            .name(ticker.getExchange())
            .code(ticker.getExchange())
            .countryCode("RU")
            .currency(Currency.getInstance("RUB"))
            .build());
        Company company = companyRepository.save(Company.builder()
            .name(ticker.getSymbol())
            .build());
        return stockRepository.save(Stock.builder()
            .instrumentType(InstrumentType.STOCK.name())
            .company(company)
            .exchange(exchange)
            .ticker(ticker.getSymbol())
            .currency(exchange.getCurrency())
            .build());
    }
}
