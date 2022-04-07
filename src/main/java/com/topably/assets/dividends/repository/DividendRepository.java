package com.topably.assets.dividends.repository;

import com.topably.assets.dividends.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    @Query(nativeQuery = true, value = "select d.*\n" +
            "from dividend d\n" +
            "         join security s on s.id = d.security_id\n" +
            "         join exchange e on e.id = s.exchange_id\n" +
            "where d.declare_date is not null\n" +
            "  and s.ticker = :ticker\n" +
            "  and e.code = :exchange\n" +
            "order by d.declare_date desc\n" +
            "limit 1")
    Dividend findLastDeclaredDividend(String ticker, String exchange);
}
