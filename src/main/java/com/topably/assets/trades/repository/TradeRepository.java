package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.Trade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query(value = "from Trade t " +
            "join t.portfolioHolding holding " +
            "join Portfolio p on p.id = holding.portfolio.id and p.id = :portfolioId " +
            "join fetch t.portfolioHolding.instrument instrument " +
            "join fetch t.portfolioHolding.instrument.exchange exchange " +
            "where exists(select d from Dividend d where d.instrument.id = t.portfolioHolding.instrument.id) " +
            "order by t.date")
    Collection<Trade> findDividendPayingTradesOrderByTradeDate(Long portfolioId);

    @Query(value = """
            select t from Trade t
            join fetch t.portfolioHolding ph
            join fetch ph.instrument i
            join fetch i.exchange exch
            join fetch t.broker br
            join ph.portfolio p
            join p.user u
            where u.id = :userId
            """)
    Collection<Trade> findAllByUserId(Long userId);

    Collection<Trade> findAllByPortfolioHolding_IdOrderByDate(Long holdingId);
}
