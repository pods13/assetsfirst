package com.topably.assets.portfolios.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PortfolioPositionRepository extends JpaRepository<PortfolioPosition, Long> {

    Optional<PortfolioPosition> findByPortfolio_User_IdAndInstrument_Id(Long userId, Long instrumentId);

    @EntityGraph(attributePaths = {"instrument", "tags", "tags.category"})
    List<PortfolioPosition> findAllByPortfolioId(Long portfolioId);

    @EntityGraph(attributePaths = {"instrument", "tags", "tags.category"})
    List<PortfolioPosition> findAllByPortfolioIdAndOpenDateLessThanEqual(Long portfolioId, LocalDate date);

    @EntityGraph(attributePaths = {"instrument", "tags", "tags.category"})
    @Query(
        """
        select p from PortfolioPosition p
        where p.portfolio.id = :portfolioId and p.quantity > 0
        """
    )
    List<PortfolioPosition> findAllNotSoldByPortfolioId(Long portfolioId);

    @Query(
        """
        SELECT pos.id from PortfolioPosition pos
        join pos.portfolio p
        where p.id = :portfolioId
        """
    )
    List<Long> findAllPositionIdsByPortfolioId(Long portfolioId);

    List<PortfolioPosition> findAllByQuantityIsAndRealizedPnlIsNull(BigInteger quantity);

    Collection<PortfolioPosition> findAllByPortfolioIdAndTagsIn(Long portfolioId, Collection<Long> tagIds);

    @Query(
        """
            select pos from PortfolioPosition pos
                  join pos.portfolio p
                  join Trade t on t.portfolioPosition.id = pos.id
            where p.id = :portfolioId and year(t.date) = :year and t.operation = com.topably.assets.trades.domain.TradeOperation.SELL
        """
    )
    Collection<PortfolioPosition> findPositionsWithSellTradesByYear(Long portfolioId, int year);

}
