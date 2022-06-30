package com.topably.assets.dividends.service;

import com.topably.assets.core.domain.TickerSymbol;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class DividendServiceImpl implements DividendService {

    private final InstrumentService instrumentService;
    private final DividendRepository dividendRepository;

    @Override
    public Collection<Dividend> findDividends(String ticker, String exchange) {
        return dividendRepository.findByInstrument_TickerAndInstrument_Exchange_CodeOrderByRecordDateAsc(ticker, exchange);
    }

    @Override
    @Transactional
    public void addDividends(String ticker, String exchange, Collection<DividendData> dividendData) {
        deleteForecastedDividends(ticker, exchange);
        var instrumentDividends = collectDividendsToPersist(ticker, exchange, dividendData);
        dividendRepository.upsertAll(instrumentDividends);
    }

    @Override
    public BigDecimal calculateAnnualDividend(TickerSymbol tickerSymbol, Year year) {
        return dividendRepository.findDividendsByYear(tickerSymbol.getSymbol(), tickerSymbol.getExchange(), year.getValue())
                .stream()
                .map(Dividend::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Dividend> collectDividendsToPersist(String ticker, String exchange,
                                                     Collection<DividendData> dividendData) {
        Dividend lastDeclaredDividend = dividendRepository.findLastDeclaredDividend(ticker, exchange);
        var instrument = Optional.ofNullable(lastDeclaredDividend)
                .map(Dividend::getInstrument)
                .orElseGet(() -> instrumentService.findInstrument(ticker, exchange));
        return dividendData.stream()
                .filter(data -> lastDeclaredDividend == null || afterLastDeclaredDividend(lastDeclaredDividend, data.getDeclareDate()))
                .filter(data -> data.getAmount().compareTo(BigDecimal.ZERO) > 0)
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
