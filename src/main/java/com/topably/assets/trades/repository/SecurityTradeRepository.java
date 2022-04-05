package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.SecurityTrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityTradeRepository extends JpaRepository<SecurityTrade, Long> {
}
