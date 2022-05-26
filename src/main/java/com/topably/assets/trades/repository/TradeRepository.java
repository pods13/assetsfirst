package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.Trade;
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
}
