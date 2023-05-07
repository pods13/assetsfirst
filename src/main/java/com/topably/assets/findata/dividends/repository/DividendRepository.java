package com.topably.assets.findata.dividends.repository;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.core.repository.UpsertRepository;
import com.topably.assets.findata.dividends.domain.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface DividendRepository extends JpaRepository<Dividend, Long>, UpsertRepository<Dividend, Long> {

    @Query(value = """
        select * from dividend d
        join instrument i on i.id = d.instrument_id
        join exchange e on e.id = i.exchange_id
        where ((d.pay_date is not null and year(d.pay_date) in :dividendYears)
            or (d.pay_date is null and year(adddate(d.record_date, interval 1 month)) in :dividendYears))
          and i.ticker = :#{#ticker.symbol}
          and e.code = :#{#ticker.exchange}
        """, nativeQuery = true)
    Collection<Dividend> findDividendsByYears(Ticker ticker, Iterable<Integer> dividendYears);

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

    Optional<Dividend> findTopByRecordDateBeforeOrderByRecordDateDesc(LocalDate date);
}
