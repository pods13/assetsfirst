package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.repository.ExchangeRateRepository;
import com.topably.assets.xrates.service.currency.CurrencyService;
import com.topably.assets.xrates.service.provider.ExchangeProvider;
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
@CacheConfig(cacheNames = "exchange-rates", cacheManager = "longLivedCacheManager")
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeProvider cbrExchangeProvider;
    private final CurrencyService currencyService;

    @Override
    public Collection<ExchangeRate> addExchangeRates(List<ExchangeRate> rates) {
        return exchangeRateRepository.upsertAll(rates);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable
    public Optional<ExchangeRate> findExchangeRate(Currency from, Currency to, Instant time) {
        LocalDate date = time.atZone(ZoneId.systemDefault()).toLocalDate();
        return Optional.ofNullable(exchangeRateRepository.findBySourceCurrencyAndDestinationCurrencyAndDate(from, to, date)
            .orElseGet(() -> {
                var fetchedExchangeRates = fetchExchangeRates(time);
                Collection<ExchangeRate> exchangeRates = addExchangeRates(fetchedExchangeRates);
                return fetchedExchangeRates.stream()
                    .filter(rate -> from.equals(rate.getSourceCurrency())).findFirst().orElse(null);
            }));
    }

    @Override
    public List<ExchangeRate> fetchExchangeRates(Instant exchangeRatesForTime) {
        return cbrExchangeProvider.getExchangeRates(exchangeRatesForTime, currencyService.getAvailableCurrencies());
    }
}
