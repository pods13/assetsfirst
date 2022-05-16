package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.SecurityTrade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SecurityTradeRepository extends JpaRepository<SecurityTrade, Long> {

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    Collection<SecurityTrade> findAllByUser_Username(String username);

    @EntityGraph(attributePaths = {"instrument", "instrument.exchange"})
    Collection<SecurityTrade> findAllByUser_UsernameAndInstrument_InstrumentType(String username, String instrumentType);

    @Query(value = "from SecurityTrade t " +
            "join User u on u.username = :username " +
            "join fetch t.instrument instrument " +
            "join fetch t.instrument.exchange exchange " +
            "where exists(select d from Dividend d where d.instrument.id = t.instrument.id) " +
            "order by t.date")
    Collection<SecurityTrade> findUserDividendPayingTradesOrderByTradeDate(String username);
}
