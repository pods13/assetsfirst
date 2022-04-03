package com.topably.assets.exchanges.repository;

import com.topably.assets.exchanges.domain.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
