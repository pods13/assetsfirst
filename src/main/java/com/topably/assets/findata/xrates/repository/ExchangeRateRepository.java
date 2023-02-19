package com.topably.assets.findata.xrates.repository;

import com.topably.assets.core.repository.UpsertRepository;
import com.topably.assets.findata.xrates.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long>, UpsertRepository<ExchangeRate, Long> {

    Collection<ExchangeRate> findAllByDestinationCurrency(Currency destinationCurrency);

    Optional<ExchangeRate> findBySourceCurrencyAndDestinationCurrencyAndDate(Currency from, Currency to, LocalDate date);
}
