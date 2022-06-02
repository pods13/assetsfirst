package com.topably.assets.portfolios.repository;

import com.topably.assets.portfolios.domain.PortfolioHolding;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {

    Optional<PortfolioHolding> findByPortfolio_User_UsernameAndInstrument_Id(String username, Long instrumentId);

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    List<PortfolioHolding> findAllByPortfolioId(Long portfolioId);
}
