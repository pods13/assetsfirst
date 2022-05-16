package com.topably.assets.instruments.repository.security;

import com.topably.assets.instruments.domain.ETF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ETFRepository extends JpaRepository<ETF, Long> {
}
