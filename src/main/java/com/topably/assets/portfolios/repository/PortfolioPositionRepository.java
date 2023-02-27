package com.topably.assets.portfolios.repository;

import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortfolioPositionRepository extends JpaRepository<PortfolioPosition, Long> {

    Optional<PortfolioPosition> findByPortfolio_User_IdAndInstrument_Id(Long userId, Long instrumentId);

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange", "tags"})
    List<PortfolioPosition> findAllByPortfolioId(Long portfolioId);

    @Query("select p.id from PortfolioPosition p")
    List<Long> findAllPositionIdsByPortfolioId(Long portfolioId);
}
