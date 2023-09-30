package com.topably.assets.findata.xrates.service.provider;

import com.topably.assets.findata.xrates.domain.ExchangeRate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.fx.FxSymbols;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

@Service
public class YFExchangeProvider implements ExchangeProvider {

    private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Moscow");

    @Override
    public List<ExchangeRate> getExchangeRates(Instant time) {
        return getExchangeRates(time, Collections.emptySet());
    }

    @Override
    public List<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain) {
        var date = time.atZone(DEFAULT_TIMEZONE).toLocalDate();

        var fxQuotes = toFXQuotes(sourceCurrenciesToObtain);
        try {
            return YahooFinance.getFx(fxQuotes.toArray(String[]::new)).values().stream()
                .map(quote -> {
                    var srcByDestCurrencies = fxSymbolToSrcByDestCurrencies(quote.getSymbol());
                    return new ExchangeRate()
                        .setSourceCurrency(Currency.getInstance(srcByDestCurrencies.getFirst()))
                        .setDestinationCurrency(Currency.getInstance(srcByDestCurrencies.getSecond()))
                        .setConversionRate(quote.getPrice())
                        .setDate(date);
                })
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> toFXQuotes(Collection<Currency> sourceCurrenciesToObtain) {
        return sourceCurrenciesToObtain.stream()
            .map(c -> {
                if ("USD".equals(c.getCurrencyCode())) {
                    return List.of(FxSymbols.USDEUR, FxSymbols.USDHKD, "USDCNY=X");
                } else if ("EUR".equals(c.getCurrencyCode())) {
                    return List.of(FxSymbols.EURUSD, FxSymbols.EURHKD, "EURCNY=X");
                } else if ("HKD".equals(c.getCurrencyCode())) {
                    return List.of(FxSymbols.HKDUSD, FxSymbols.HKDEUR, "HKDCNY=X");
                } else if ("CNY".equals(c.getCurrencyCode())) {
                    return List.of("CNYUSD=X", "CNYEUR=X", "CNYHKD=X");
                }
                return Collections.<String>emptyList();
            })
            .flatMap(Collection::stream)
            .toList();
    }

    private Pair<String, String> fxSymbolToSrcByDestCurrencies(String symbol) {
        return Pair.of(symbol.substring(0, 3), symbol.substring(3, 6));
    }
}
