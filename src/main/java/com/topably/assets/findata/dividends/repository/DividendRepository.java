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
        where ((d.pay_date is not null and year(d.pay_date) in :dividendYears)
            or (d.pay_date is null and year(d.record_date) in :dividendYears))
          and i.symbol = :#{#ticker.symbol}
          and i.exchange_code = :#{#ticker.exchange}
        """, nativeQuery = true)
    Collection<Dividend> findDividendsByYears(Ticker ticker, Iterable<Integer> dividendYears);

    @Query(nativeQuery = true, value = """
        select d.*
        from dividend d
                 join instrument i on i.id = d.instrument_id
        where d.declare_date is not null
          and i.symbol = :symbol
          and i.exchange_code = :exchange
        order by d.declare_date desc
        limit 1
        """)
    Dividend findLastDeclaredDividend(String symbol, String exchange);

    Collection<Dividend> findAllByDeclareDateIsNullAndInstrument_SymbolAndInstrument_ExchangeCode(String symbol, String exchange);

    @Query(value = """
        select d.* from dividend d
        join instrument i on i.id = d.instrument_id
        where i.symbol = :#{#ticker.symbol}
          and i.exchange_code = :#{#ticker.exchange}
          and d.record_date < :date
          order by d.record_date desc
          limit 1
        """, nativeQuery = true)
    Optional<Dividend> findTopByRecordDateBeforeOrderByRecordDateDesc(Ticker ticker, LocalDate date);

    @Query(value = """
        select d.* from dividend d
        join instrument i on i.id = d.instrument_id
        where ((d.pay_date is not null and d.pay_date between :startDate and :endDate)
            or (d.pay_date is null and adddate(d.record_date, interval 1 month) between :startDate and :endDate))
          and i.id = :instrumentId
        """, nativeQuery = true)
    List<Dividend> findAllPaidDividendsByInstrumentId(Long instrumentId, LocalDate startDate, LocalDate endDate);

    @Query(nativeQuery = true, value = """
        select d.*
        from dividend d
                 join instrument i on i.id = d.instrument_id
        where i.symbol = :symbol
          and i.exchange_code = :exchange
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
            where concat(i.symbol, '.', i.exchangeCode) in (:tickers)
              and d.recordDate >= current_date
              and d.amount > 0
        """)
    Page<Dividend> findUpcomingDividends(List<String> tickers, Pageable pageable);

    List<Dividend> findAllByInstrument_Id(Long instrumentId);
}
