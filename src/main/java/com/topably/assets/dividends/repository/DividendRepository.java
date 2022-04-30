package com.topably.assets.dividends.repository;

import com.topably.assets.dividends.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    Collection<Dividend> findBySecurity_TickerAndSecurity_Exchange_CodeOrderByPayDateAsc(String ticker, String exchange);

    @Query(nativeQuery = true, value = "select d.*\n" +
            "from dividend d\n" +
            "         join security s on s.id = d.security_id\n" +
            "         join exchange e on e.id = s.exchange_id\n" +
            "where year(d.declare_date) = :year\n" +
            "  and s.ticker = :ticker\n" +
            "  and e.code = :exchange\n")
    Collection<Dividend> findDeclaredYearlyDividends(String ticker, String exchange, int year);

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
