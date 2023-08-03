package com.topably.assets.findata.dividends.repository;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.core.repository.UpsertRepository;
import com.topably.assets.findata.dividends.domain.Dividend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DividendRepository extends JpaRepository<Dividend, Long>, UpsertRepository<Dividend, Long> {

    @Query(value = """
        select d.* from dividend d
        join instrument i on i.id = d.instrument_id
        join exchange e on e.id = i.exchange_id
        where ((d.pay_date is not null and year(d.pay_date) in :dividendYears)
            or (d.pay_date is null and year(adddate(d.record_date, interval 1 month)) in :dividendYears))
          and i.ticker = :#{#ticker.symbol}
          and e.code = :#{#ticker.exchange}
        """, nativeQuery = true)
    Collection<Dividend> findDividendsByYears(Ticker ticker, Iterable<Integer> dividendYears);

    @Query(nativeQuery = true, value = """
        select d.*
        from dividend d
                 join instrument i on i.id = d.instrument_id
                 join exchange e on e.id = i.exchange_id
        where d.declare_date is not null
          and i.ticker = :symbol
          and e.code = :exchange
        order by d.declare_date desc
        limit 1
        """)
    Dividend findLastDeclaredDividend(String symbol, String exchange);

    Collection<Dividend> findAllByDeclareDateIsNullAndInstrument_TickerAndInstrument_Exchange_Code(String ticker, String exchange);

    Optional<Dividend> findTopByRecordDateBeforeOrderByRecordDateDesc(LocalDate date);

    List<Dividend> findAllByInstrumentIdAndRecordDateGreaterThanEqual(Long instrumentId, LocalDate date);

    @Query(nativeQuery = true, value = """
        select d.*
        from dividend d
                 join instrument i on i.id = d.instrument_id
                 join exchange e on e.id = i.exchange_id
        where i.ticker = :symbol
          and e.code = :exchange
          and d.record_date >= now()
          and d.amount > 0
        order by d.record_date
        limit 1
        """)
    Optional<Dividend> findUpcomingDividend(String symbol, String exchange);

    @EntityGraph(attributePaths = {"instrument"})
    @Query(value = """
        select d from Dividend d
            join d.instrument as i
            join i.exchange
            where concat(i.ticker, '.', i.exchange.code) in (:tickers)
              and d.recordDate >= current_date
              and d.amount > 0
            group by d.id
        """)
    Page<Dividend> findUpcomingDividends(List<String> tickers, Pageable pageable);
}
