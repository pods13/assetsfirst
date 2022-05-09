package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.SecurityTrade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SecurityTradeRepository extends JpaRepository<SecurityTrade, Long> {

    @EntityGraph(attributePaths = {"security", "security.exchange"})
    Collection<SecurityTrade> findAllByUser_Username(String username);

    @EntityGraph(attributePaths = {"security", "security.exchange"})
    Collection<SecurityTrade> findAllByUser_UsernameAndSecurity_SecurityType(String username, String securityType);

    @Query(value = "from SecurityTrade t " +
            "join User u on u.username = :username " +
            "join fetch t.security security " +
            "join fetch t.security.exchange exchange " +
            "where exists(select d from Dividend d where d.security.id = t.security.id) " +
            "order by t.date")
    Collection<SecurityTrade> findUserDividendPayingTradesOrderByTradeDate(String username);
}
