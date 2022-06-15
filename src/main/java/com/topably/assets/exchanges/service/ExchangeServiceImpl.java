package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.service.InstrumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@CacheConfig(cacheNames = "exchanges")
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private static final Set<String> US_EXCHANGE_CODES = Arrays.stream(USExchange.values()).map(USExchange::name).collect(toSet());

    private final ExchangeRepository exchangeRepository;
    private final InstrumentService instrumentService;

    @Override
    @Transactional
    public Collection<TickerSymbol> findTickersByExchange(String exchange) {
        var exchangeCodes = "US".equals(exchange) ? US_EXCHANGE_CODES : Set.of(exchange);
        var securityTypes = Set.of(InstrumentType.STOCK, InstrumentType.ETF);
        var securities = instrumentService.findCertainTypeOfInstrumentsByExchangeCodes(securityTypes, exchangeCodes);
        return securities.stream()
                .map(security -> new TickerSymbol(security.getTicker(), security.getExchange().getCode()))
                .collect(toList());
    }

    @Override
    @Cacheable(sync = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
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
