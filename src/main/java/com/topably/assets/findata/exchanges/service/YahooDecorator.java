package com.topably.assets.findata.exchanges.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Component
@Slf4j
public class YahooDecorator {
    private static final Set<String> US_EXCHANGE_CODES = Stream.of(ExchangeEnum.NYSE, ExchangeEnum.NYSEARCA, ExchangeEnum.NASDAQ)
            .map(ExchangeEnum::name).collect(toSet());

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Optional<BigDecimal> findSymbolRecentPriceOnYahoo(Ticker ticker) {
        try {
            var stock = YahooFinance.get(convertToYahooFinanceSymbol(ticker));
            return Optional.ofNullable(stock).map(s -> s.getQuote().getPrice());
        } catch (IOException e) {
            log.warn("Cannot receive recent prices for {} from yahooFinance", ticker);
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
