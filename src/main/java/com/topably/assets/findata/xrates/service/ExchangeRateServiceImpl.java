package com.topably.assets.findata.xrates.service;

import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.findata.xrates.domain.ExchangeRate;
import com.topably.assets.findata.xrates.repository.ExchangeRateRepository;
import com.topably.assets.findata.xrates.service.provider.ExchangeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = CacheNames.EXCHANGE_RATES_LL, cacheManager = "longLivedCacheManager")
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeProvider freeCurrencyProvider;

    @Override
    public Collection<ExchangeRate> addExchangeRates(List<ExchangeRate> rates) {
        if (CollectionUtils.isEmpty(rates)) {
            log.warn("Attempted to add empty or null exchange rates");
            return Collections.emptyList();
        }
        return exchangeRateRepository.upsertAll(rates);
    }

    @Override
    @Cacheable(unless = "#result == null")
    public Optional<ExchangeRate> findExchangeRate(Currency from, Currency to, Instant time) {
        LocalDate date = time.atZone(ZoneId.systemDefault()).toLocalDate();
        var rate = exchangeRateRepository.findBySourceCurrencyAndDestinationCurrencyAndDate(from, to, date)
                .orElseGet(() -> fetchAndStoreExchangeRate(from, to, time).orElse(null));
        return Optional.ofNullable(rate);
    }

    private Optional<ExchangeRate> fetchAndStoreExchangeRate(Currency from, Currency to, Instant time) {
        log.info("Cache miss - fetching exchange rate from {} to {} for date {}", from, to, time);

        try {
            List<ExchangeRate> fetchedRates = freeCurrencyProvider.getExchangeRates(time, List.of(from));
            addExchangeRates(fetchedRates);

            return fetchedRates.stream()
                    .filter(rate -> from.equals(rate.getSourceCurrency()) && to.equals(rate.getDestinationCurrency()))
                    .findFirst();
        } catch (Exception e) {
            log.error("Failed to fetch exchange rate from {} to {}: {}", from, to, e.getMessage(), e);
            return Optional.empty();
        }
    }

}
