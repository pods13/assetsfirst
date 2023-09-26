package com.topably.assets.findata.exchanges.repository;

import com.topably.assets.findata.exchanges.domain.InstrumentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface InstrumentPriceRepository extends JpaRepository<InstrumentPrice, Long> {

    @Query(value = """
        select ip.*
        from instrument_price ip
                 join instrument i on i.id = ip.instrument_id
                 join exchange e on e.id = i.exchange_id
        where i.symbol = :symbol
          and e.code = :exchange
        order by datetime desc
        limit 1
        """, nativeQuery = true)
    Optional<InstrumentPrice> findTopByTickerOrderByDatetimeDesc(String symbol, String exchange);


    @Query(value = """
        select ip.*
        from instrument_price ip
                 join instrument i on i.id = ip.instrument_id
                 join exchange e on e.id = i.exchange_id
        where i.symbol = :symbol
          and e.code = :exchange
          and cast(datetime as date) = :date
        order by datetime desc
        limit 1
        """, nativeQuery = true)
    Optional<InstrumentPrice> findTopByTickerAndDateOrderByDatetimeDesc(String symbol, String exchange, LocalDate date);
}
