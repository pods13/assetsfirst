package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.SecurityTrade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SecurityTradeRepository extends JpaRepository<SecurityTrade, Long> {

    @EntityGraph(attributePaths = {"security", "security.exchange"})
    Collection<SecurityTrade> findAllByUser_Username(String username);

    @Query(nativeQuery = true, value = "select t.*\n" +
            "from security_trade t\n" +
            "         join user u on u.id = t.user_id\n" +
            "         join security s on s.id = t.security_id and s.security_type in ('STOCK', 'ETF')\n" +
            "where u.username = :username\n" +
            "  and exists(select 1\n" +
            "             from dividend d\n" +
            "             where d.security_id = t.security_id)\n" +
            "order by t.date")
    Collection<SecurityTrade> findUserDividendPayingTradesOrderByTradeDate(String username);
}
