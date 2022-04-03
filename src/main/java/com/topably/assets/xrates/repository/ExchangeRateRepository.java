package com.topably.assets.xrates.repository;

import com.topably.assets.xrates.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Collection<ExchangeRate> findAllByDestinationCurrency(Currency destinationCurrency);

    Optional<ExchangeRate> findBySourceCurrencyAndDestinationCurrency(Currency from, Currency to);
}
