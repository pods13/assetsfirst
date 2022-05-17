package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.Trade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    Collection<Trade> findAllByUser_Username(String username);

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    Collection<Trade> findAllByUser_UsernameAndInstrument_InstrumentType(String username, String instrumentType);

    @Query(value = "from Trade t " +
            "join User u on u.username = :username " +
            "join fetch t.instrument instrument " +
            "join fetch t.instrument.exchange exchange " +
            "where exists(select d from Dividend d where d.instrument.id = t.instrument.id) " +
            "order by t.date")
    Collection<Trade> findUserDividendPayingTradesOrderByTradeDate(String username);
}
