package com.topably.assets.dividends.service.integration;

import com.topably.assets.dividends.domain.dto.DividendData;
import com.topably.assets.dividends.service.provider.DividendProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class NasdaqIntegrationJobLauncher {

    private final DividendProvider nasdaqDividendProvider;

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 1000)
    public void launchCBRIntegrationJob() {
        Collection<DividendData> dividendHistory = nasdaqDividendProvider.getDividendHistory("ADM");
    }
}
