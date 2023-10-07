package com.topably.assets.findata.xrates.service.provider;

import com.topably.assets.findata.xrates.domain.ExchangeRate;
import com.topably.assets.findata.xrates.domain.fc.CurrencyHistoricalExchangeRateData;
import com.topably.assets.findata.xrates.service.provider.client.FreeCurrencyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FreeCurrencyProvider implements ExchangeProvider {

    private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FreeCurrencyClient client;


    private final String apiKey;

    public FreeCurrencyProvider(FreeCurrencyClient client, @Value("${app.integration.free-currency.key}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    @Override
    public List<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain) {
        return sourceCurrenciesToObtain.stream()
            .map(baseCurrency -> collectExchangeRates(time, baseCurrency))
            .flatMap(Collection::stream)
            .toList();
    }

    private List<ExchangeRate> collectExchangeRates(Instant time, Currency baseCurrency) {
        var date = time.atZone(DEFAULT_TIMEZONE).toLocalDate();
        return fetchExchangeRates(date, baseCurrency)
            .entrySet().stream()
            .map(dstCurrencyByRate -> {
                return new ExchangeRate()
                    .setSourceCurrency(baseCurrency)
                    .setDestinationCurrency(Currency.getInstance(dstCurrencyByRate.getKey()))
                    .setConversionRate(dstCurrencyByRate.getValue())
                    .setDate(date);
            })
            .toList();
    }

    private Map<String, BigDecimal> fetchExchangeRates(LocalDate date, Currency baseCurrency) {
        var currencyCode = baseCurrency.getCurrencyCode();
        try {
            if (date.compareTo(LocalDate.now()) >= 0) {
                return client.getLatest(apiKey, currencyCode).data();
            } else {
                var historicalDate = date.format(DATE_TIME_FORMATTER);
                var result = client.getHistorical(apiKey, currencyCode, historicalDate);
                return result.data().get(historicalDate);
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError() && HttpStatusCode.valueOf(429).equals(e.getStatusCode())) {
                log.error("We reached limits for free currency api", e);
                //TODO replace api key with new one and reinvoke api
                return Collections.emptyMap();
            }
            throw e;
        }
    }
}
