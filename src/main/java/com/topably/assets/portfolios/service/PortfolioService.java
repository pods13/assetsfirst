package com.topably.assets.portfolios.service;

import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioDto;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.domain.dto.PortfolioValuesByDates;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = CacheNames.PORTFOLIOS_LL, cacheManager = "longLivedCacheManager")
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;
    private final PortfolioPositionService portfolioPositionService;

    public Portfolio findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public BigDecimal calculateCurrentAmount(Portfolio portfolio) {
        //TODO calculate portfolio value by some date in the past
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
        return calculateCurrentAmount(positions);
    }

    public BigDecimal calculateCurrentAmountInYieldInstrument(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
            .filter(p -> !InstrumentType.FX.name().equals(p.getInstrumentType()))
            .toList();
        return calculateCurrentAmount(positions);
    }

    private BigDecimal calculateCurrentAmount(Collection<PortfolioPositionDto> positions) {
        return positions.stream()
            .map(p -> {
                var marketValue = exchangeService.findSymbolRecentPrice(p.getIdentifier())
                    .map(value -> value.multiply(new BigDecimal(p.getQuantity())))
                    .orElse(p.getTotal());
                return currencyConverterService.convert(marketValue, p.getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateInvestedAmount(Portfolio portfolio) {
        var nextDay = LocalDate.now().plus(1, ChronoUnit.DAYS);
        return calculateInvestedAmountByDate(portfolio, nextDay);
    }

    @Cacheable(key = "{ #root.methodName, #portfolio.id, #date }")
    public BigDecimal calculateInvestedAmountByDate(Portfolio portfolio, LocalDate date) {
        var positions = portfolioPositionService.findPortfolioPositionsOpenedByDate(portfolio.getId(), date);
        return calculateInvestedAmount(portfolio, positions, date);
    }

    public BigDecimal calculateInvestedAmountInYieldInstrument(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
            .filter(p -> !InstrumentType.FX.name().equals(p.getInstrumentType()))
            .toList();
        var nextDay = LocalDate.now().plus(1, ChronoUnit.DAYS);
        return calculateInvestedAmount(portfolio, positions, nextDay);
    }

    public BigDecimal calculateInvestedAmount(Portfolio portfolio, Collection<PortfolioPositionDto> positions, LocalDate date) {
        //TODO take portfolio.currency from portfolio instance
        Currency portfolioCurrency = Currency.getInstance("RUB");
        return positions.stream()
            .map(p -> portfolioPositionService.calculateInvestedAmountByPositionId(p.getId(), portfolioCurrency, date))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calculateAnnualDividend(Portfolio portfolio, Year year) {
        var positions = portfolioPositionService.findPortfolioPositionsByPortfolioId(portfolio.getId());
        return positions.stream()
            .map(p -> {
                var dividendPerShare = portfolioPositionService.calculateAnnualDividend(p, year);
                return currencyConverterService.convert(dividendPerShare.multiply(new BigDecimal(p.getQuantity())), p.getInstrument().getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public PortfolioValuesByDates getInvestedAmountByDates(Portfolio portfolio, int numOfDatesBetween) {
        var endDate = LocalDate.now().plusDays(1);
        var start = endDate.minusYears(1);
        var datesBetween = getDatesBetween(start, endDate, numOfDatesBetween);
        var dates = datesBetween.stream()
            .map(Objects::toString)
            .toList();
        var values = datesBetween.stream()
            .map(d -> calculateInvestedAmountByDate(portfolio, d))
            .toList();
        return new PortfolioValuesByDates(dates, values);
    }

    public List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate, int numOfDatesBetween) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        var datesBeforeEndStream = IntStream.iterate(0, i -> (numOfDaysBetween - 1) <= numOfDatesBetween ? i + 1 : i + (int) Math.ceil((double) numOfDaysBetween / numOfDatesBetween))
            .limit(numOfDatesBetween)
            .mapToObj(startDate::plusDays);
        return Stream.concat(datesBeforeEndStream, Stream.of(endDate)).toList();
    }

    @Transactional(readOnly = true)
    public PortfolioDto getPortfolioInfo(String identifier) {
        //TODO Get rid of hardcoded value
        var portfolio = portfolioRepository.getReferenceById(1L);
        return new PortfolioDto()
            .setValueIncreasePct(calculatePortfolioValueIncreasePct(portfolio))
            //TODO use portfolio currency instead
            .setCurrencyCode(Currency.getInstance("RUB").getCurrencyCode())
            .setInvestedAmountByDates(getInvestedAmountByDates(portfolio, 15));
    }

    private BigDecimal calculatePortfolioValueIncreasePct(Portfolio portfolio) {
        var invested = calculateInvestedAmount(portfolio);
        var current = calculateCurrentAmount(portfolio);
        return current.subtract(invested).divide(invested, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
