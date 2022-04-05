package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.ETFTrade;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface ETFTradeRepository extends JpaRepository<ETFTrade, Long> {
}
