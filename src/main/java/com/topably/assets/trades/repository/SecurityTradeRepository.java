package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.security.SecurityTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SecurityTradeRepository extends JpaRepository<SecurityTrade, Long> {

    @Query(nativeQuery = true, value = "select * from security_trade t join user u on u.id = t.user_id where u.username = :username")
    Collection<SecurityTrade> findAllByUsername(String username);
}
