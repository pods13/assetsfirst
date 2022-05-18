package com.topably.assets.instruments.repository.instrument;

import com.topably.assets.instruments.domain.instrument.ETF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ETFRepository extends JpaRepository<ETF, Long> {
}
