package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private static final Set<String> US_EXCHANGE_CODES = Arrays.stream(USExchange.values()).map(USExchange::name).collect(toSet());

    private final ExchangeRepository exchangeRepository;
    private final SecurityService securityService;

    @Override
    @Transactional
    public Collection<TickerSymbol> findTickersByExchange(String exchange) {
        var exchangeCodes = "US".equals(exchange) ? US_EXCHANGE_CODES : Set.of(exchange);
        var securityTypes = Set.of(SecurityType.STOCK, SecurityType.ETF);
        var securities = securityService.findCertainTypeOfSecuritiesByExchangeCodes(securityTypes, exchangeCodes);
        return securities.stream()
                .map(security -> new TickerSymbol(security.getTicker(), security.getExchange().getCode()))
                .collect(toList());
    }

    @Override
    public Optional<BigDecimal> findTickerRecentPrice(TickerSymbol symbol) {
        try {
            var stock = YahooFinance.get(convertToYahooFinanceSymbol(symbol));
            return Optional.ofNullable(stock).map(s -> s.getQuote().getPrice());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String convertToYahooFinanceSymbol(TickerSymbol tickerSymbol) {
        if (US_EXCHANGE_CODES.contains(tickerSymbol.getExchange())) {
            return tickerSymbol.getSymbol();
        } else if ("XETRA".equals(tickerSymbol.getExchange())) {
            return tickerSymbol.getSymbol() + ".DE";
        } else if ("MCX".equals(tickerSymbol.getExchange())) {
            return tickerSymbol.getSymbol() + ".ME";
        }
        return tickerSymbol.toString();
    }
}
