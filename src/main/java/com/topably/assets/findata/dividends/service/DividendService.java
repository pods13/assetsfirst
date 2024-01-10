package com.topably.assets.findata.dividends.service;

import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.dividends.domain.dto.AggregatedDividendDto;
import com.topably.assets.findata.dividends.domain.dto.DividendData;
import com.topably.assets.findata.dividends.repository.DividendRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
@CacheConfig(cacheNames = CacheNames.DIVIDENDS_LL, cacheManager = "longLivedCacheManager")
public class DividendService {

    private final DividendRepository dividendRepository;
    private final InstrumentService instrumentService;

    public void addDividends(String symbol, String exchange, Collection<DividendData> dividendData) {
        deleteForecastedDividends(symbol, exchange);
        var instrumentDividends = collectDividendsToPersist(symbol, exchange, dividendData);
        dividendRepository.upsertAll(instrumentDividends);
    }

    @Cacheable(key = "{ #root.methodName, #ticker, #year }")
    public BigDecimal calculateAnnualDividend(Ticker ticker, Year year) {
        var selectedYear = year.getValue();
        var latestDividendYear = getLatestDividendYear(ticker, selectedYear);
        var dividendYears = Optional.ofNullable(latestDividendYear)
            .map(dividendYear -> Set.of(selectedYear, dividendYear, dividendYear - 1))
            .orElseGet(() -> Set.of(selectedYear));
        var dividends = dividendRepository.findDividendsByYears(ticker, dividendYears);
        var dividendsByYear = groupDividendsByRecordDate(dividends);
        var selectedYearDividends = dividendsByYear.get(selectedYear);
        if (CollectionUtils.isEmpty(selectedYearDividends)) {
            if (latestDividendYear == null || selectedYear < Year.now().getValue()) {
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

    private Integer getLatestDividendYear(Ticker ticker, int selectedYear) {
        return dividendRepository.findTopByRecordDateBeforeOrderByRecordDateDesc(ticker, LocalDate.ofYearDay(selectedYear, 1))
            .map(Dividend::getRecordDate)
            .map(LocalDate::getYear)
            .orElse(null);
    }

    private Map<Integer, List<Dividend>> groupDividendsByRecordDate(Collection<Dividend> dividends) {
        return dividends.stream()
            .collect(Collectors.groupingBy(d -> d.getRecordDate().getYear(), collectingAndThen(toList(),
                divs -> divs.stream().sorted(Comparator.comparing(Dividend::getRecordDate)).toList())));
    }

    private List<Dividend> collectDividendsToPersist(String symbol, String exchange,
                                                     Collection<DividendData> dividendData) {
        Dividend lastDeclaredDividend = dividendRepository.findLastDeclaredDividend(symbol, exchange);
        var instrument = Optional.ofNullable(lastDeclaredDividend)
            .map(Dividend::getInstrument)
            .orElseGet(() -> instrumentService.findInstrument(symbol, exchange));
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

    private void deleteForecastedDividends(String symbol, String exchange) {
        Collection<Dividend> forecastedDividends = dividendRepository.findAllByDeclareDateIsNullAndInstrument_SymbolAndInstrument_Exchange_Code(symbol, exchange);
        dividendRepository.deleteAll(forecastedDividends);
    }

    public Collection<AggregatedDividendDto> aggregateDividends(Collection<Trade> trades, Collection<Integer> dividendYears) {
        var groupedTrades = trades.stream()
            .collect(groupingBy(trade -> {
                var instrument = trade.getPortfolioPosition().getInstrument();
                return instrument.toTicker();
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

    @Transactional(readOnly = true)
    public BigDecimal calculateAccumulatedDividends(PortfolioPosition position, Collection<AggregatedTradeDto.TradeData> tradeData) {
        var instrumentId = position.getInstrument().getId();
        var dividendRecordDate = position.getOpenDate().plus(2, ChronoUnit.DAYS);
        var dividends = dividendRepository.findAllPaidDividendsByInstrumentId(instrumentId, dividendRecordDate, LocalDate.now());
        return dividends.stream()
            .map(d -> {
                var shares = countShares(tradeData, d);
                return d.getAmount().multiply(shares);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal countShares(Collection<AggregatedTradeDto.TradeData> tradeData, Dividend dividend) {
        return tradeData.stream()
            .filter(trade -> trade.getTradeTime().plus(2, ChronoUnit.DAYS).compareTo(dividend.getRecordDate()) <= 0)
            .map(AggregatedTradeDto.TradeData::getShares)
            .map(BigDecimal::new)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    @Cacheable
    public Optional<Dividend> findUpcomingDividend(Ticker ticker) {
        return dividendRepository.findUpcomingDividend(ticker.getSymbol(), ticker.getExchange());
    }

    @Transactional(readOnly = true)
    public Page<Dividend> findUpcomingDividends(Collection<Ticker> tickers, Pageable pageable) {
        return dividendRepository.findUpcomingDividends(tickers.stream().map(Ticker::toString).toList(), pageable);
    }
}
