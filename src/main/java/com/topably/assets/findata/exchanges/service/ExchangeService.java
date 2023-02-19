package com.topably.assets.findata.exchanges.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.findata.exchanges.domain.InstrumentPrice;
import com.topably.assets.findata.exchanges.domain.USExchange;
import com.topably.assets.findata.exchanges.repository.ExchangeRepository;
import com.topably.assets.findata.exchanges.repository.InstrumentPriceRepository;
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
public class ExchangeService {

    private static final Set<String> US_EXCHANGE_CODES = Arrays.stream(USExchange.values())
        .map(USExchange::name).collect(toSet());
    private static final Set<String> DEFAULT_INSTRUMENT_TYPES = Stream.of(InstrumentType.values())
        .map(InstrumentType::name).collect(toSet());

    private final ExchangeRepository exchangeRepository;
    private final InstrumentPriceRepository priceRepository;

    public Page<Ticker> getSymbols(Pageable pageable, Set<String> instrumentTypes, boolean inAnyPortfolio) {
        return exchangeRepository.findInstrumentsOfCertainTypesByExchangeCodes(pageable, null,
            useDefaultInstrumentTypesIfNull(instrumentTypes), inAnyPortfolio);
    }

    private Set<String> useDefaultInstrumentTypesIfNull(Set<String> instrumentTypes) {
        return Optional.ofNullable(instrumentTypes).orElse(DEFAULT_INSTRUMENT_TYPES);
    }

    @Transactional
    public Page<Ticker> getSymbolsByExchange(String exchange, Pageable pageable, Set<String> instrumentTypes,
                                             boolean inAnyPortfolio) {
        var exchangeCodes = "US".equals(exchange) ? US_EXCHANGE_CODES : Set.of(exchange);
        return exchangeRepository.findInstrumentsOfCertainTypesByExchangeCodes(pageable, exchangeCodes,
            useDefaultInstrumentTypesIfNull(instrumentTypes), inAnyPortfolio);
    }

    @Cacheable
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<BigDecimal> findSymbolRecentPrice(Ticker ticker) {
        if (ExchangeEnum.FX_IDC.name().equals(ticker.getExchange())) {
            return Optional.empty();
        }
        return priceRepository.findTopByTickerOrderByDatetimeDesc(ticker.getSymbol(), ticker.getExchange())
            .map(InstrumentPrice::getValue)
            .or(() -> findSymbolRecentPriceOnYahoo(ticker));
    }

    private Optional<BigDecimal> findSymbolRecentPriceOnYahoo(Ticker ticker) {
        try {
            var stock = YahooFinance.get(convertToYahooFinanceSymbol(ticker));
            return Optional.ofNullable(stock).map(s -> s.getQuote().getPrice());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String convertToYahooFinanceSymbol(Ticker ticker) {
        if (US_EXCHANGE_CODES.contains(ticker.getExchange())) {
            return ticker.getSymbol();
        } else if (ExchangeEnum.XETRA.name().equals(ticker.getExchange())) {
            return ticker.getSymbol() + ".DE";
        } else if (ExchangeEnum.MCX.name().equals(ticker.getExchange())) {
            return ticker.getSymbol() + ".ME";
        }
        return ticker.toString();
    }
}
