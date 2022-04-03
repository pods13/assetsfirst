package com.topably.assets.xrates.service;

import com.topably.assets.xrates.domain.ExchangeRate;
import com.topably.assets.xrates.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public Collection<ExchangeRate> addExchangeRates(Collection<ExchangeRate> rates) {
        return exchangeRateRepository.saveAll(rates);
    }

    @Override
    @Transactional
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
    public BigDecimal convertCurrency(BigDecimal amount, Currency from, Currency to) {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findBySourceCurrencyAndDestinationCurrency(from, to);
        return exchangeRate
                .map(ExchangeRate::getConversionRate)
                .map(amount::multiply)
                .orElseThrow();
    }
}
