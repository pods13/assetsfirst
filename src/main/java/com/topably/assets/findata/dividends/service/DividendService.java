package com.topably.assets.findata.dividends.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.dividends.domain.dto.AggregatedDividendDto;
import com.topably.assets.findata.dividends.domain.dto.DividendData;
import com.topably.assets.findata.dividends.repository.DividendRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "dividends", cacheManager = "longLivedCacheManager")
public class DividendService {

    private final DividendRepository dividendRepository;
    private final InstrumentService instrumentService;

    public void addDividends(String ticker, String exchange, Collection<DividendData> dividendData) {
        deleteForecastedDividends(ticker, exchange);
        var instrumentDividends = collectDividendsToPersist(ticker, exchange, dividendData);
        dividendRepository.upsertAll(instrumentDividends);
    }

    @Cacheable(key = "{ #root.methodName, #positionId, #instrument.toTicker(), #year }")
    public BigDecimal calculateAnnualDividend(Long positionId, Instrument instrument, Year year) {
        var instrumentType = instrument.getInstrumentType();
        if (!InstrumentType.STOCK.name().equals(instrumentType) && !InstrumentType.ETF.name().equals(instrumentType)) {
            return BigDecimal.ZERO;
        }
        var ticker = instrument.toTicker();
        var selectedYear = year.getValue();
        var latestDividendYear = getLatestDividendYear(selectedYear);
        var dividendYears = Optional.ofNullable(latestDividendYear)
            .map(dividendYear -> Set.of(selectedYear, dividendYear, dividendYear - 1))
            .orElseGet(() -> Set.of(selectedYear));
        var dividends = dividendRepository.findDividendsByYears(ticker, dividendYears);
        var dividendsByYear = groupDividendsByRecordDate(dividends);
        var selectedYearDividends = dividendsByYear.get(selectedYear);
        if (selectedYearDividends == null || selectedYearDividends.isEmpty()) {
            if (latestDividendYear == null) {
                return BigDecimal.ZERO;
            }
            return Optional.ofNullable(dividendsByYear.get(latestDividendYear))
                .filter(d -> !d.isEmpty())
                .map(d -> d.stream().map(Dividend::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
        }

        var lastRecorded = selectedYearDividends.get(selectedYearDividends.size() - 1);
        var recordedDividendAmount = selectedYearDividends.stream().map(Dividend::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var lastRecordDate = lastRecorded.getRecordDate().plus(2, ChronoUnit.WEEKS);
        var isRecordedForFullYear = lastRecordDate.getYear() > selectedYear;
        if (isRecordedForFullYear) {
            return recordedDividendAmount;
        }

        var firstRecordDate = selectedYearDividends.get(0).getRecordDate();
        var projectedDividendsByPrevYearsData = dividends.stream()
            .filter(d -> d.getRecordDate().compareTo(lastRecordDate.minusYears(1)) >= 0
                && d.getRecordDate().compareTo(firstRecordDate) < 0)
            .map(Dividend::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return recordedDividendAmount.add(projectedDividendsByPrevYearsData);
    }

    private Integer getLatestDividendYear(int selectedYear) {
        return dividendRepository.findTopByRecordDateBeforeOrderByRecordDateDesc(LocalDate.ofYearDay(selectedYear, 1))
            .map(Dividend::getRecordDate)
            .map(LocalDate::getYear)
            .orElse(null);
    }

    private Map<Integer, List<Dividend>> groupDividendsByRecordDate(Collection<Dividend> dividends) {
        return dividends.stream()
            .collect(Collectors.groupingBy(d -> d.getRecordDate().getYear(), collectingAndThen(toList(),
                divs -> divs.stream().sorted(Comparator.comparing(Dividend::getRecordDate)).toList())));
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
        return new Dividend()
            .setInstrument(instrument)
            .setAmount(data.getAmount())
            .setDeclareDate(data.getDeclareDate())
            .setRecordDate(data.getRecordDate())
            .setPayDate(data.getPayDate());
    }

    private void deleteForecastedDividends(String ticker, String exchange) {
        Collection<Dividend> forecastedDividends = dividendRepository.findAllByDeclareDateIsNullAndInstrument_TickerAndInstrument_Exchange_Code(ticker, exchange);
        dividendRepository.deleteAll(forecastedDividends);
    }

    public Collection<AggregatedDividendDto> aggregateDividends(Collection<Trade> trades, Collection<Integer> dividendYears) {
        var groupedTrades = trades.stream()
            .collect(groupingBy(trade -> {
                var instrument = trade.getPortfolioPosition().getInstrument();
                return new Ticker(instrument.getTicker(), instrument.getExchange().getCode());
            }));

        return groupedTrades.entrySet().stream()
            .map(tradesByTicker -> composeAggregatedDividends(tradesByTicker, dividendYears))
            .flatMap(Collection::stream)
            .filter(divDetails -> divDetails.getTotal().compareTo(BigDecimal.ZERO) > 0)
            .toList();
    }

    private Collection<AggregatedDividendDto> composeAggregatedDividends(Map.Entry<Ticker, List<Trade>> tradesByTicker,
                                                                         Collection<Integer> dividendYears) {
        var ticker = tradesByTicker.getKey();
        var quantity = BigInteger.ZERO;
        var aggregatedDividends = new ArrayList<AggregatedDividendDto>();
        var trades = tradesByTicker.getValue();
        var currency = trades.iterator().hasNext() ? trades.iterator().next().getPortfolioPosition().getInstrument().getCurrency() : null;
        int index = 0;
        for (Dividend dividend : dividendRepository.findDividendsByYears(ticker, dividendYears)) {
            for (; index < trades.size(); index++) {
                Trade trade = trades.get(index);
                if (trade.getDate().compareTo(dividend.getRecordDate()) < 0) {
                    var operationQty = TradeOperation.SELL.equals(trade.getOperation()) ? trade.getQuantity().negate() : trade.getQuantity();
                    quantity = quantity.add(operationQty);
                } else {
                    break;
                }
            }
            BigDecimal total = dividend.getAmount().multiply(new BigDecimal(quantity));
            var forecasted = dividend.getPayDate() == null;
            var payDate = Optional.ofNullable(dividend.getPayDate())
                .orElseGet(() -> dividend.getRecordDate().plus(1, ChronoUnit.MONTHS));
            aggregatedDividends.add(new AggregatedDividendDto()
                .setTicker(ticker)
                .setCurrency(currency)
                .setForecasted(forecasted)
                .setPayDate(payDate)
                .setTotal(total)
            );
        }
        return aggregatedDividends;
    }

}
