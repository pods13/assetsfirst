package com.topably.assets.portfolios.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.core.config.demo.DemoDataConfig;
import com.topably.assets.core.util.NumberUtils;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverter;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioShortInfoDto;
import com.topably.assets.portfolios.domain.dto.pub.PortfolioInfoDto;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.domain.dto.PortfolioValuesByDates;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = CacheNames.PORTFOLIOS_LL, cacheManager = "longLivedCacheManager")
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ExchangeService exchangeService;
    private final CurrencyConverter currencyConverter;
    private final PortfolioPositionService portfolioPositionService;
    private final DemoDataConfig demoDataConfig;

    public Portfolio findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    @Cacheable(key = "{ #root.methodName, #portfolio.id, #date }")
    public BigDecimal calculateMarketValueByDate(Portfolio portfolio, LocalDate date) {
        var positions = portfolioPositionService.findPortfolioPositionsOpenedByDate(portfolio.getId(), date);
        return calculateMarketValueByPositions(portfolio, positions, date);
    }

    public BigDecimal calculateMarketValueInYieldInstruments(Portfolio portfolio, LocalDate date) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
            .filter(p -> !InstrumentType.FX.name().equals(p.getInstrumentType()))
            .toList();
        return calculateMarketValueByPositions(portfolio, positions, date);
    }

    private BigDecimal calculateMarketValueByPositions(Portfolio portfolio, Collection<PortfolioPositionDto> positions, LocalDate date) {
        return positions.stream()
            .map(p -> {
                var marketValue = exchangeService.findSymbolPriceByDate(p.getIdentifier(), date)
                    .map(value -> value.multiply(new BigDecimal(p.getQuantity())))
                    .orElse(p.getTotal());
                return currencyConverter.convert(marketValue, p.getCurrency(), portfolio.getCurrency(), date.atStartOfDay().toInstant(
                    ZoneOffset.UTC));
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
        Currency portfolioCurrency = portfolio.getCurrency();
        return positions.stream()
            .map(p -> portfolioPositionService.calculateInvestedAmountByPositionId(p.getId(), portfolioCurrency, date))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal calculateAnnualDividend(Portfolio portfolio, Year year) {
        var positions = portfolioPositionService.findPortfolioPositionsByPortfolioId(portfolio.getId());
        return positions.stream()
            .map(p -> {
                var dividendPerShare = portfolioPositionService.calculateAnnualDividend(p, year);
                return currencyConverter.convert(dividendPerShare.multiply(new BigDecimal(p.getQuantity())),
                    p.getInstrument().getCurrency(),
                    portfolio.getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public PortfolioValuesByDates getMarketValueByDates(Portfolio portfolio, int numOfDatesBetween) {
        return calculatePortfolioValueByDates(portfolio, numOfDatesBetween, this::calculateMarketValueByDate);
    }

    public PortfolioValuesByDates getInvestedValueByDates(Portfolio portfolio, int numOfDatesBetween) {
        return calculatePortfolioValueByDates(portfolio, numOfDatesBetween, this::calculateInvestedAmountByDate);
    }

    public PortfolioValuesByDates calculatePortfolioValueByDates(
        Portfolio portfolio,
        int numOfDatesBetween,
        BiFunction<Portfolio, LocalDate, BigDecimal> calcFunc
    ) {
        var endDate = LocalDate.now().plusDays(1);
        var start = endDate.minusYears(1);
        var datesBetween = getDatesBetween(start, endDate, numOfDatesBetween);
        var dates = datesBetween.stream()
            .map(Objects::toString)
            .toList();
        var values = datesBetween.stream()
            .map(d -> calcFunc.apply(portfolio, d))
            .toList();
        return new PortfolioValuesByDates(dates, values);
    }

    public List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate, int numOfDatesBetween) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        var datesBeforeEndStream = IntStream.iterate(0,
                i -> (numOfDaysBetween - 1) <= numOfDatesBetween ? i + 1 :
                    i + (int) Math.ceil((double) numOfDaysBetween / numOfDatesBetween))
            .limit(numOfDatesBetween)
            .mapToObj(startDate::plusDays);
        return Stream.concat(datesBeforeEndStream, Stream.of(endDate)).toList();
    }

    @Transactional(readOnly = true)
    public PortfolioInfoDto getPortfolioInfo(CurrentUser user, String identifier) {
        var portfolio = findPortfolioByIdentifier(user, identifier);
        return new PortfolioInfoDto()
            .setValueIncreasePct(calculatePortfolioValueIncreasePct(portfolio))
            .setCurrencyCode(portfolio.getCurrency().getCurrencyCode())
            //TODO start persisting calculated invested and market values into database
            .setInvestedValueByDates(getInvestedValueByDates(portfolio, 3))
            .setMarketValueByDates(getMarketValueByDates(portfolio, 15));
    }

    private BigDecimal calculatePortfolioValueIncreasePct(Portfolio portfolio) {
        var invested = calculateInvestedAmount(portfolio);
        var current = calculateMarketValueByDate(portfolio, LocalDate.now());
        return NumberUtils.calculatePercentage(invested, current.subtract(invested));
    }

    public Portfolio findPortfolioByIdentifier(CurrentUser user, String identifier) {
        if (demoDataConfig.getUsername().equals(identifier)) {
            return portfolioRepository.findByUser_Username(demoDataConfig.getUsername()).orElseThrow();
        } else if (user != null && user.getUsername().equals(identifier)) {
            return portfolioRepository.findByUser_Username(user.getUsername()).orElseThrow();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Transactional(readOnly = true)
    public PortfolioShortInfoDto getPortfolioShortInfoByUser(CurrentUser user) {
        var portfolio = portfolioRepository.findByUser_Username(user.getUsername()).orElseThrow();
        return new PortfolioShortInfoDto()
            .setCurrencyCode(portfolio.getCurrency().getCurrencyCode())
            .setInvestedValue(calculateInvestedAmount(portfolio));
    }

}
