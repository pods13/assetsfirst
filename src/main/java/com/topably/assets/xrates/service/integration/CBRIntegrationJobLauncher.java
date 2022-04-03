package com.topably.assets.xrates.service.integration;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.service.ExchangeRateService;
import com.topably.assets.xrates.service.provider.CBRExchangeProvider;
import com.topably.assets.xrates.service.provider.ExchangeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CBRIntegrationJobLauncher {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final ExchangeProvider cbrExchangeProvider = new CBRExchangeProvider(DESTINATION_CURRENCY);
    private final ExchangeRateService exchangeRateService;

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1000)
    public void launchCBRIntegrationJob() {
        List<ExchangeRate> exchangeRates = cbrExchangeProvider.getExchangeRates(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        exchangeRateService.updateExchangeRates(DESTINATION_CURRENCY, exchangeRates);
    }

}
