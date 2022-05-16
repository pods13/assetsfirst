package com.topably.assets.dividends.repository;

import com.topably.assets.dividends.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface DividendRepository extends JpaRepository<Dividend, Long> {

    Collection<Dividend> findByInstrument_TickerAndInstrument_Exchange_CodeOrderByRecordDateAsc(String ticker, String exchange);

    @Query(nativeQuery = true, value = "select d.*\n" +
            "from dividend d\n" +
            "         join instrument i on i.id = d.instrument_id\n" +
            "         join exchange e on e.id = i.exchange_id\n" +
            "where year(d.record_date) = :year\n" +
            "  and i.ticker = :ticker\n" +
            "  and e.code = :exchange\n")
    Collection<Dividend> findDividendsByYear(String ticker, String exchange, int year);

    @Query(nativeQuery = true, value = "select d.*\n" +
            "from dividend d\n" +
            "         join instrument i on i.id = d.instrument_id\n" +
            "         join exchange e on e.id = i.exchange_id\n" +
            "where d.declare_date is not null\n" +
            "  and i.ticker = :ticker\n" +
            "  and e.code = :exchange\n" +
            "order by d.declare_date desc\n" +
            "limit 1")
    Dividend findLastDeclaredDividend(String ticker, String exchange);

    Collection<Dividend> findAllByDeclareDateIsNullAndInstrument_TickerAndInstrument_Exchange_Code(String ticker, String exchange);
}
