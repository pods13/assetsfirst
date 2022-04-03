package com.topably.assets.portfolios.repository;

import com.topably.assets.portfolios.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Collection<Portfolio> findByUserId(Long userId);
}
