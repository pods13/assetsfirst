package com.topably.assets.portfolios.service;

import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Currency;

@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "portfolios", cacheManager = "longLivedCacheManager")
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;
    private final PortfolioPositionService portfolioPositionService;
    private final DividendService dividendService;

    public Portfolio findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public BigDecimal calculateCurrentAmount(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
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
        //TODO add position.openDate to filter out positions opened after date
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
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
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
        return positions.stream()
            .map(h -> {
                var dividendPerShare = dividendService.calculateAnnualDividend(h.getIdentifier(), year);
                return currencyConverterService.convert(dividendPerShare.multiply(new BigDecimal(h.getQuantity())), h.getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
