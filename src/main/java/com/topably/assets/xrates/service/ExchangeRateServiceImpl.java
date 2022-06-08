package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.repository.ExchangeRateRepository;
import com.topably.assets.xrates.service.currency.CurrencyService;
import com.topably.assets.xrates.service.provider.ExchangeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
@CacheConfig(cacheNames = "exchange-rates", cacheManager = "longLivedCacheManager")
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeProvider cbrExchangeProvider;
    private final CurrencyService currencyService;

    @Override
    public Collection<ExchangeRate> addExchangeRates(Collection<ExchangeRate> rates) {
        return exchangeRateRepository.saveAll(rates);
    }

    @Override
    @Transactional
    @Cacheable
    public Optional<ExchangeRate> findExchangeRate(Currency from, Currency to) {
        return Optional.ofNullable(exchangeRateRepository.findBySourceCurrencyAndDestinationCurrency(from, to)
                .orElseGet(() -> {
                    Collection<ExchangeRate> fetchExchangeRates = fetchExchangeRates(LocalDate.now());
                    return addExchangeRates(fetchExchangeRates).stream()
                            .filter(rate -> from.equals(rate.getSourceCurrency())).findFirst().orElse(null);
                }));
    }

    @Override
    public Collection<ExchangeRate> fetchExchangeRates(LocalDate exchangeRatesForDate) {
        Instant time = exchangeRatesForDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        return cbrExchangeProvider.getExchangeRates(time, currencyService.getAvailableCurrencies());
    }
}
