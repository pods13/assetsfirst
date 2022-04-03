package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.money.MoneyTrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyTradeRepository extends JpaRepository<MoneyTrade, Long> {
}
