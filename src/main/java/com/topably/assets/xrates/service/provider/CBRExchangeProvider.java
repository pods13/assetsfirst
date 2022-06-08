package com.topably.assets.xrates.service.provider;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.domain.cbr.CBRCurrencyData;
import com.topably.assets.xrates.domain.cbr.CBRExchangeRateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class CBRExchangeProvider implements ExchangeProvider {

    private static final String DEFAULT_ENDPOINT = "https://www.cbr.ru/scripts/XML_daily.asp";
    private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final XmlMapper mapper = new XmlMapper();
    private final Currency destinationCurrency = Currency.getInstance("RUB");

    public CBRExchangeProvider() {
    }

    @Override
    public Collection<ExchangeRate> getExchangeRates(Instant time) {
        return getExchangeRates(time, Collections.emptySet());
    }

    @Override
    public Collection<ExchangeRate> getExchangeRates(Instant time, Collection<Currency> sourceCurrenciesToObtain) {
        LocalDate date = time.atZone(DEFAULT_TIMEZONE).toLocalDate();
        var url = generateUrl(date);
        log.info("Trying to get exchange rates from cbr endpoint, url='{}'", url);
        var data = getExchangeRateData(createHttpClient(), url);
        List<CBRCurrencyData> currencies = ofNullable(data)
                .map(CBRExchangeRateData::getCurrencies)
                .orElse(emptyList());
        return currencies.stream()
                .filter(currency -> sourceCurrenciesToObtain.contains(Currency.getInstance(currency.getCharCode())))
                .map(currency -> {
                    var source = Currency.getInstance(currency.getCharCode());
                    BigDecimal conversionRate = currency.getValue()
                            .divide(BigDecimal.valueOf(currency.getNominal()), RoundingMode.HALF_UP);
                    return ExchangeRate.builder()
                            .sourceCurrency(source)
                            .destinationCurrency(destinationCurrency)
                            .conversionRate(conversionRate)
                            .date(date)
                            .build();
                }).collect(toList());
    }

    private CBRExchangeRateData getExchangeRateData(HttpClient httpClient, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), CBRExchangeRateData.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String generateUrl(LocalDate date) {
        return DEFAULT_ENDPOINT + "?date_req=" + date.format(DATE_TIME_FORMATTER);
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}
