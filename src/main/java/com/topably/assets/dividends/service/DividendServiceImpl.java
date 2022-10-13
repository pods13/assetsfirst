package com.topably.assets.dividends.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.domain.dto.DividendData;
import com.topably.assets.dividends.repository.DividendRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.service.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class DividendServiceImpl implements DividendService {

    private final InstrumentService instrumentService;
    private final DividendRepository dividendRepository;

    @Override
    public Collection<Dividend> findDividends(String ticker, String exchange) {
        return dividendRepository.findByInstrument_TickerAndInstrument_Exchange_CodeOrderByRecordDateAsc(ticker, exchange);
    }

    @Override
    public void addDividends(String ticker, String exchange, Collection<DividendData> dividendData) {
        deleteForecastedDividends(ticker, exchange);
        var instrumentDividends = collectDividendsToPersist(ticker, exchange, dividendData);
        dividendRepository.upsertAll(instrumentDividends);
    }

    @Override
    public BigDecimal calculateAnnualDividend(Ticker ticker, Year year) {
        return dividendRepository.findDividendsByYears(ticker, year.getValue())
                .stream()
                .map(Dividend::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateProbableAnnualDividend(Ticker ticker, Year year) {
        var selectedYear = year.getValue();
        var dividendsByYear = dividendRepository.findDividendsByYears(ticker, selectedYear, selectedYear - 1, selectedYear - 2)
                .stream()
                .collect(Collectors.groupingBy(d -> d.getRecordDate().getYear(), collectingAndThen(toList(),
                        divs -> divs.stream().sorted(Comparator.comparing(Dividend::getRecordDate)).toList())));
        var selectedYearDividends = dividendsByYear.get(selectedYear);
        if (selectedYearDividends == null || selectedYearDividends.isEmpty()) {
            return Optional.ofNullable(dividendsByYear.get(selectedYear - 1)).filter(d -> !d.isEmpty())
                    .or(() -> Optional.ofNullable(dividendsByYear.get(selectedYear - 2)))
                    .map(d -> d.stream().map(Dividend::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                    .orElse(BigDecimal.ZERO);
        }

        var lastRecorded = selectedYearDividends.get(selectedYearDividends.size() - 1);
        var recordedDividendAmount = selectedYearDividends.stream().map(Dividend::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var date = lastRecorded.getRecordDate().plus(2, ChronoUnit.WEEKS);
        if (date.getYear() > selectedYear) {
            return recordedDividendAmount;
        }
        //TODO fix case when in prev year divs were cancelled -> look into dividends of {selectedYear - 2} year
        return recordedDividendAmount.add(Optional.ofNullable(dividendsByYear.get(selectedYear - 1))
                .orElse(Collections.emptyList())
                .stream()
                .filter(d -> d.getRecordDate().compareTo(date) > 0)
                .map(Dividend::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private List<Dividend> collectDividendsToPersist(String ticker, String exchange,
                                                     Collection<DividendData> dividendData) {
        Dividend lastDeclaredDividend = dividendRepository.findLastDeclaredDividend(ticker, exchange);
        var instrument = Optional.ofNullable(lastDeclaredDividend)
                .map(Dividend::getInstrument)
                .orElseGet(() -> instrumentService.findInstrument(ticker, exchange));
        return dividendData.stream()
                .filter(data -> lastDeclaredDividend == null || afterLastDeclaredDividend(lastDeclaredDividend, data.getDeclareDate()))
                .map(data -> convertToDividend(data, instrument))
                .collect(toList());
    }

    private boolean afterLastDeclaredDividend(Dividend lastDeclaredDividend, LocalDate declaredDate) {
        if (declaredDate == null) {
            return true;
        }
        return declaredDate.compareTo(lastDeclaredDividend.getDeclareDate()) > 0;
    }

    private Dividend convertToDividend(DividendData data, Instrument instrument) {
        return Dividend.builder()
                .instrument(instrument)
                .amount(data.getAmount())
                .declareDate(data.getDeclareDate())
                .recordDate(data.getRecordDate())
                .payDate(data.getPayDate())
                .build();
    }

    private void deleteForecastedDividends(String ticker, String exchange) {
        Collection<Dividend> forecastedDividends = dividendRepository.findAllByDeclareDateIsNullAndInstrument_TickerAndInstrument_Exchange_Code(ticker, exchange);
        dividendRepository.deleteAll(forecastedDividends);
    }

}
