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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
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
        return exchangeRateRepository.upsertAll(rates);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(unless = "#result != null")
    public Optional<ExchangeRate> findExchangeRate(Currency from, Currency to, Instant time) {
        LocalDate date = time.atZone(ZoneId.systemDefault()).toLocalDate();
        return Optional.ofNullable(exchangeRateRepository.findBySourceCurrencyAndDestinationCurrencyAndDate(from, to, date)
            .orElseGet(() -> {
                var fetchedExchangeRates = freeCurrencyProvider.getExchangeRates(time, List.of(from));
                Collection<ExchangeRate> exchangeRates = addExchangeRates(fetchedExchangeRates);
                return fetchedExchangeRates.stream()
                    .filter(rate -> from.equals(rate.getSourceCurrency()) && to.equals(rate.getDestinationCurrency())).findFirst().orElse(null);
            }));
    }
}
