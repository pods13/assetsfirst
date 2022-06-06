package com.topably.assets.portfolios.repository;

import com.topably.assets.portfolios.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Portfolio findByUserId(Long userId);
}
