package com.topably.assets.fundamentals.service;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.fundamentals.domain.FundamentalsDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FundamentalsService {

    private final PortfolioHoldingService portfolioHoldingService;
    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;

    @Transactional(readOnly = true)
    public Collection<FundamentalsDto> findPortfolioHoldingsFundamentals(Long userId) {
        var holdings = portfolioHoldingService.findPortfolioHoldingsByUserId(userId);

        return holdings.stream()
                .map(holding -> {
                    var marketValue = exchangeService.findTickerRecentPrice(holding.getIdentifier())
                            .map(value -> value.multiply(new BigDecimal(holding.getQuantity())))
                            .orElse(holding.getTotal());
                    var convertedMarketValue = currencyConverterService.convert(marketValue, holding.getCurrency());
                    return FundamentalsDto.builder()
                            .identifier(holding.getIdentifier())
                            .marketValue(marketValue)
                            .convertedMarketValue(convertedMarketValue)
                            .build();
                }).toList();
    }
}
