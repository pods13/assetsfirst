package com.topably.assets.portfolios.service;

import com.topably.assets.auth.service.UserService;
import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Currency;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final UserService userService;
    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;
    private final PortfolioPositionService portfolioPositionService;
    private final DividendService dividendService;

    public Portfolio createDefaultUserPortfolio(Long userId) {
        var dashboard = PortfolioDashboard.builder()
            .cards(new HashSet<>())
            .build();
        Portfolio portfolio = Portfolio.builder()
            .user(userService.getById(userId))
            .dashboard(dashboard)
            .build();
        return portfolioRepository.save(portfolio);
    }

    public Portfolio findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public BigDecimal calculateCurrentAmount(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
        return positions.stream()
            .map(h -> {
                var marketValue = exchangeService.findSymbolRecentPrice(h.getIdentifier())
                    .map(value -> value.multiply(new BigDecimal(h.getQuantity())))
                    .orElse(h.getTotal());
                return currencyConverterService.convert(marketValue, h.getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateInvestedAmount(Portfolio portfolio) {
        var positions = portfolioPositionService.findPortfolioPositions(portfolio.getId());
        Currency portfolioCurrency = Currency.getInstance("RUB");
        return positions.stream()
            //TODO optimize it, calculate not by each position but rather by all of them
            .map(p -> portfolioPositionService.calculateInvestedAmountByPositionId(p.getId(), portfolioCurrency))
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
