package com.topably.assets.dividends.repository;

import com.topably.assets.dividends.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendRepository extends JpaRepository<Dividend, Long> {
}
