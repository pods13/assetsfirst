package com.topably.assets.dividends.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.stream.IntStream;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.dividends.repository.DividendRepository;
import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto.DeltaPnl;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto.TradeData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;


@IT
public class DividendServiceTest extends IntegrationTestBase {

    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private DividendService dividendService;

    @Nested
    class CalculateAnnualDividend {

        @Test
        public void givenStockThatNeverPaidDividends_whenDividendAmountCalculated_thenReturnZeroAmount() {
            var ticker = new Ticker("TSLA", "NYSE");
            var instrument = createStockInstrument(ticker);

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2022));

            assertThat(actualAmount).isZero();
        }

        @Test
        public void givenOnlyOneYearDividends_whenDividendAmountCalculatedForFutureYear_thenReturnExistedYearAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var expectedAmount = BigDecimal.TEN;
            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                .setAmount(expectedAmount)
                .setInstrument(instrument)
                .setRecordDate(LocalDate.of(2019, 12, 6))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2024).plusYears(1));

            assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
        }

        @Test
        public void givenDividendsForDifferentStocks_whenDividendAmountCalculatedForFutureYear_thenReturnExistedYearAmountForSpecificStock() {
            var ticker = new Ticker("TEST", ExchangeEnum.NYSE.name());
            var instrument = createStockInstrument(ticker);
            var firstDivAmount = BigDecimal.TEN;
            var secondDivAmount = BigDecimal.TEN;

            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(firstDivAmount)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2023, 9, 7))
                    .setPayDate(LocalDate.of(2023, 9, 21)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(secondDivAmount)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2023, 10, 30))
                    .setPayDate(LocalDate.of(2023, 12, 22)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.ONE)
                    .setInstrument(createStockInstrument(new Ticker("NONE", "MCX")))
                    .setRecordDate(LocalDate.of(2024, 9, 6)
                    ))
            );

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2024).plusYears(1));

            assertThat(actualAmount).isEqualByComparingTo(firstDivAmount.add(secondDivAmount));
        }

        @Test
        public void givenFirstTimeDividends_whenDividendAmountCalculatedForSameYear_thenReturnCorrectAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var expectedAmount = BigDecimal.TEN;
            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                .setAmount(expectedAmount)
                .setInstrument(instrument
                ).setRecordDate(LocalDate.of(2021, 4, 23))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2021));

            assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
        }


        @Test
        public void givenThisYearDividendsNotFullyPaidAndDataForPrevYearPresented_whenDividendAmountCalculatedForSameYear_thenReturnCorrectAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var expectedAmount = BigDecimal.valueOf(30L);
            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2020, 4, 23)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2020, 2, 23)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2019, 8, 21)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2019, 4, 23))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2020));

            assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
        }

        @Test
        public void givenNextYearDividendsPredicted_whenDividendAmountCalculated_thenReturnedTTMAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var first = BigDecimal.valueOf(207.31);
            var second = BigDecimal.valueOf(414.61);
            var expectedAmount = first.add(second);
            addDividendData(List.of(new Dividend()
                    .setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(second)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2024, 6, 16)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(first)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2023, 10, 7)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.valueOf(267.48))
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2021, 10, 11)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.valueOf(387.15))
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2021, 6, 7))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2024));

            assertThat(actualAmount).isEqualByComparingTo(expectedAmount);
        }

        @Test
        public void givenDividends_whenDividendAmountCalculatedForPassedYearWithoutDividends_thenReturnedZero() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var first = BigDecimal.valueOf(207.31);
            var second = BigDecimal.valueOf(414.61);
            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(second)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2024, 6, 16)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(first)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2023, 10, 7)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.valueOf(267.48))
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2021, 10, 11)),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.valueOf(387.15))
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2021, 6, 7))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2022));

            assertThat(actualAmount).isZero();
        }

        @Test
        public void givenMonthlyPayedDividends_whenDividendAmountCalculated_thenReturnedCorrectAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);

            var monthlyDividends = IntStream.range(0, 12)
                .mapToObj(i -> {
                    var date = LocalDate.of(2023, i + 1, 1);
                    return new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                        .setAmount(BigDecimal.TEN)
                        .setInstrument(instrument)
                        .setRecordDate(date.with(TemporalAdjusters.lastDayOfMonth()));
                }).toList();
            addDividendData(monthlyDividends);
            addDividendData(List.of(new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2024, 1, 1).with(TemporalAdjusters.lastDayOfMonth())),
                new Dividend().setUnadjustedAmount(BigDecimal.ZERO)
                    .setAmount(BigDecimal.TEN)
                    .setInstrument(instrument)
                    .setRecordDate(LocalDate.of(2024, 2, 1).with(TemporalAdjusters.lastDayOfMonth()))));

            var actualAmount = dividendService.calculateAnnualDividend(ticker, Year.of(2025));

            assertThat(actualAmount).isEqualByComparingTo(BigDecimal.TEN.multiply(new BigDecimal(12L)));
        }

    }

    @Nested
    class CalculateAccumulatedDividends {

        @Test
        public void givenOnlyBuyTrades_whenAccumulatedDividendsCalculated_thenReturnCorrectAmount() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var dividendAmount = BigDecimal.TEN;
            addDividendData(List.of(new Dividend()
                .setUnadjustedAmount(BigDecimal.ZERO)
                .setAmount(dividendAmount)
                .setInstrument(instrument
                ).setRecordDate(LocalDate.of(2023, 12, 6))));

            var position = new PortfolioPosition()
                .setInstrument(instrument)
                .setOpenDate(LocalDate.of(2022, 9, 15));
            var ownedShares = BigInteger.valueOf(100L);
            var aggregatedTradeDto = new AggregatedTradeDto()
                .setBuyTradesData(List.of(
                    new TradeData(ownedShares,
                        BigDecimal.TEN,
                        LocalDate.of(2022, 9, 15),
                        new TradeView()))
                ).setDeltaPnls(Collections.emptyList());
            var actualAccumulated = dividendService.calculateAccumulatedDividends(position, aggregatedTradeDto);

            assertThat(actualAccumulated).isEqualByComparingTo(dividendAmount.multiply(new BigDecimal(ownedShares)));
        }

        @Test
        public void givenTrades_whenAccumulatedDividendsCalculated_thenSoldSharesAlsoIncluded() {
            var ticker = new Ticker("TEST", "MCX");
            var instrument = createStockInstrument(ticker);
            var dividendAmount = BigDecimal.TEN;
            addDividendData(List.of(new Dividend()
                .setAmount(dividendAmount)
                .setUnadjustedAmount(BigDecimal.ZERO)
                .setInstrument(instrument
                ).setRecordDate(LocalDate.of(2023, 12, 6))));

            var openDate = LocalDate.of(2022, 9, 15);
            var position = new PortfolioPosition()
                .setInstrument(instrument)
                .setOpenDate(openDate);
            var aggregatedTradeDto = new AggregatedTradeDto()
                .setBuyTradesData(List.of(
                    new TradeData(BigInteger.valueOf(100L),
                        BigDecimal.TEN,
                        openDate,
                        new TradeView()))
                ).setDeltaPnls(List.of(
                    new DeltaPnl(openDate,
                        LocalDate.of(2024, 6, 5),
                        new BigDecimal(20000L),
                        new BigDecimal(60000L),
                        BigInteger.valueOf(100L),
                        Currency.getInstance("RUB"))
                ));
            var actualAccumulated = dividendService.calculateAccumulatedDividends(position, aggregatedTradeDto);

            assertThat(actualAccumulated).isEqualByComparingTo(dividendAmount.multiply(new BigDecimal(200L)));
        }

    }

    private Collection<Dividend> addDividendData(Collection<Dividend> data) {
        return dividendRepository.saveAll(data);
    }

    private Instrument createStockInstrument(Ticker ticker) {
        return instrumentRepository.save(new Instrument()
            .setInstrumentType(InstrumentType.STOCK.name())
            .setName(ticker.getSymbol())
            .setExchangeCode(ticker.getExchange())
            .setSymbol(ticker.getSymbol())
            .setCurrency(ExchangeEnum.valueOf(ticker.getExchange()).getCurrency()));
    }

}
