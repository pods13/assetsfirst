package com.topably.assets.portfolios.repository;

import com.topably.assets.portfolios.domain.PortfolioHolding;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {

    Optional<PortfolioHolding> findByPortfolio_User_IdAndInstrument_Id(Long userId, Long instrumentId);

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    List<PortfolioHolding> findAllByPortfolioId(Long portfolioId);

    @Query("select h.id from PortfolioHolding h")
    List<Long> findAllHoldingIdsByPortfolioId(Long portfolioId);
}
