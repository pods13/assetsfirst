package com.topably.assets.xrates.service.integration;

import com.topably.assets.xrates.service.ExchangeRateService;
import com.topably.assets.xrates.service.currency.CurrencyService;
import com.topably.assets.xrates.service.provider.CBRExchangeProvider;
import com.topably.assets.xrates.service.provider.ExchangeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Currency;

@Slf4j
@Component
@RequiredArgsConstructor
public class CBRIntegrationJobLauncher {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final ExchangeProvider cbrExchangeProvider = new CBRExchangeProvider(DESTINATION_CURRENCY);
    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void launchCBRIntegrationJob() {
        Collection<Currency> sourceCurrenciesToObtain = currencyService.getAvailableCurrencies();
        Instant time = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        var exchangeRates = cbrExchangeProvider.getExchangeRates(time, sourceCurrenciesToObtain);
        exchangeRateService.updateExchangeRates(DESTINATION_CURRENCY, exchangeRates);
    }

}
