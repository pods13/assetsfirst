package com.topably.assets.exchanges.service;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.InstrumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
@CacheConfig(cacheNames = "exchanges")
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private static final Set<String> US_EXCHANGE_CODES = Arrays.stream(USExchange.values())
            .map(USExchange::name).collect(toSet());
    private static final Set<String> DEFAULT_INSTRUMENT_TYPES = Stream.of(InstrumentType.values())
            .map(InstrumentType::name).collect(toSet());

    private final ExchangeRepository exchangeRepository;

    @Override
    public Page<TickerSymbol> getTickers(Pageable pageable, Set<String> instrumentTypes) {
        return exchangeRepository.findInstrumentsOfCertainTypesByExchangeCodes(pageable, null,
                useDefaultInstrumentTypesIfNull(instrumentTypes));
    }

    private Set<String> useDefaultInstrumentTypesIfNull(Set<String> instrumentTypes) {
        return Optional.ofNullable(instrumentTypes).orElse(DEFAULT_INSTRUMENT_TYPES);
    }

    @Override
    @Transactional
    public Page<TickerSymbol> getTickersByExchange(String exchange, Pageable pageable, Set<String> instrumentTypes) {
        var exchangeCodes = "US".equals(exchange) ? US_EXCHANGE_CODES : Set.of(exchange);
        return exchangeRepository.findInstrumentsOfCertainTypesByExchangeCodes(pageable, exchangeCodes,
                useDefaultInstrumentTypesIfNull(instrumentTypes));
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
