package com.topably.assets.portfolios.service;

import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverter;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Currency;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheNames.PORTFOLIOS_LL, cacheManager = "longLivedCacheManager")
@Transactional
public class PortfolioValuationService {

    private final ExchangeService exchangeService;
    private final PortfolioPositionService portfolioPositionService;
    private final CurrencyConverter currencyConverter;

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

    @Cacheable(key = "{ #root.methodName, #portfolio.id, #date }")
    public BigDecimal calculateInvestedAmountByDate(Portfolio portfolio, LocalDate date) {
        var positions = portfolioPositionService.findPortfolioPositionsOpenedByDate(portfolio.getId(), date);
        return calculateInvestedAmount(portfolio, positions, date);
    }

    public BigDecimal calculateInvestedAmountInYieldInstrument(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
                .filter(p -> !InstrumentType.FX.name().equals(p.getInstrumentType()))
                .toList();
        var nextDay = LocalDate.now().plusDays(1L);
        return calculateInvestedAmount(portfolio, positions, nextDay);
    }

    public BigDecimal calculateInvestedAmount(Portfolio portfolio, Collection<PortfolioPositionDto> positions, LocalDate date) {
        Currency portfolioCurrency = portfolio.getCurrency();
        return positions.stream()
                .map(p -> portfolioPositionService.calculateInvestedAmountByPositionId(p.getId(), portfolioCurrency, date))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
