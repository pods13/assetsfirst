package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @CacheEvict(allEntries = true)
    public Collection<ExchangeRate> addExchangeRates(Collection<ExchangeRate> rates) {
        return exchangeRateRepository.saveAll(rates);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public Collection<ExchangeRate> updateExchangeRates(Currency destinationCurrency, Collection<ExchangeRate> rates) {
        Collection<ExchangeRate> exchangeRatesToUpdate = exchangeRateRepository.findAllByDestinationCurrency(destinationCurrency);
        if (exchangeRatesToUpdate.isEmpty()) {
            return addExchangeRates(rates);
        }
        Map<Currency, ExchangeRate> ratesBySourceCurrency = rates.stream()
                .collect(toMap(ExchangeRate::getSourceCurrency, Function.identity()));
        exchangeRatesToUpdate.forEach(rate -> {
            var newConversionRate = ratesBySourceCurrency.get(rate.getSourceCurrency()).getConversionRate();
            rate.setConversionRate(newConversionRate);
        });
        return exchangeRateRepository.saveAll(exchangeRatesToUpdate);
    }

    @Override
    @Cacheable
    public Optional<ExchangeRate> findExchangeRate(Currency from, Currency to) {
        return exchangeRateRepository.findBySourceCurrencyAndDestinationCurrency(from, to);
    }
}
