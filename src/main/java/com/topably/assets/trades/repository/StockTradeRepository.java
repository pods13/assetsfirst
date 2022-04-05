package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.StockTrade;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface StockTradeRepository extends JpaRepository<StockTrade, Long> {
}
