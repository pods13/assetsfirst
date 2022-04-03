package com.topably.assets.securities.repository.security;

import com.topably.assets.securities.domain.ETF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ETFRepository extends JpaRepository<ETF, Long> {
}
