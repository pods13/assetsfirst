package com.topably.assets.findata.splits.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.findata.splits.domain.Split;
import com.topably.assets.findata.splits.repository.SplitRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IT
public class SplitServiceTest extends IntegrationTestBase {

    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private SplitRepository splitRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SplitService splitService;

    @Test
    public void givenStockWithDividends_whenSplitHappened_thenDividendAmountIsAdjusted() {
        var ticker = new Ticker("PLZL_SPLIT", "MCX");
        var instrument = createStockInstrument(ticker);

        var dividendAmountBeforeSplit = BigDecimal.valueOf(730L);
        var splitBy = BigDecimal.TEN;
        var dividendAfterSplit = BigDecimal.valueOf(90L);
        var dividends = List.of(new Dividend()
                        .setAmount(dividendAmountBeforeSplit)
                        .setUnadjustedAmount(dividendAmountBeforeSplit)
                        .setInstrument(instrument
                        ).setRecordDate(LocalDate.of(2023, 12, 6)),
                new Dividend()
                        .setAmount(dividendAfterSplit)
                        .setUnadjustedAmount(dividendAfterSplit)
                        .setInstrument(instrument
                        ).setRecordDate(LocalDate.of(2024, 3, 6))
        );

        LocalDate splitExDate = LocalDate.of(2024, 3, 5);
        splitRepository.save(new Split().setInstrument(instrument)
                .setRatio("%s:1".formatted(splitBy))
                .setExDate(splitExDate)
                .setPayableOn(LocalDate.of(2024, 3, 1)));
        entityManager.flush();
        entityManager.clear();

        List<Dividend> res = splitService.calculateDividendAdjustedValue(dividends, instrument.getId());

        assertThat(res).hasSize(2);
        assertThat(res.getFirst().getUnadjustedAmount()).isEqualTo(dividendAmountBeforeSplit);
        assertThat(res.getFirst().getLastSplitApplied()).isEqualTo(splitExDate);
        assertThat(res.getFirst().getAmount()).isEqualTo(dividendAmountBeforeSplit.divide(splitBy, RoundingMode.HALF_EVEN));
    }

    @Test
    public void givenStockWithDividends_whenSplitHappened_thenDividendAmountIsSetToUnadjustedAmountField() {
        var ticker = new Ticker("PLZL_SPLIT", "MCX");
        var instrument = createStockInstrument(ticker);

        var dividendAmountBeforeSplit = BigDecimal.valueOf(730L);
        var splitBy = BigDecimal.TEN;
        var dividendAfterSplit = BigDecimal.valueOf(90L);
        var dividends = List.of(new Dividend()
                        .setAmount(dividendAmountBeforeSplit)
                        .setUnadjustedAmount(BigDecimal.ZERO)
                        .setInstrument(instrument
                        ).setRecordDate(LocalDate.of(2023, 12, 6)),
                new Dividend()
                        .setAmount(dividendAfterSplit)
                        .setUnadjustedAmount(BigDecimal.ZERO)
                        .setInstrument(instrument
                        ).setRecordDate(LocalDate.of(2024, 3, 6))
        );

        LocalDate exDate = LocalDate.of(2024, 3, 5);
        splitRepository.save(new Split().setInstrument(instrument)
                .setRatio("%s:1".formatted(splitBy))
                .setExDate(exDate)
                .setPayableOn(LocalDate.of(2024, 3, 1)));
        entityManager.flush();
        entityManager.clear();

        List<Dividend> res = splitService.calculateDividendAdjustedValue(dividends, instrument.getId());

        assertThat(res).hasSize(2);
        assertThat(res.getFirst().getUnadjustedAmount()).isEqualTo(dividendAmountBeforeSplit);
        assertThat(res.getFirst().getLastSplitApplied()).isEqualTo(exDate);
        assertThat(res.getFirst().getAmount()).isEqualTo(dividendAmountBeforeSplit.divide(splitBy, RoundingMode.HALF_EVEN));
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