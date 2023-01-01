package com.topably.assets.exchanges.repository;

import com.topably.assets.exchanges.domain.InstrumentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InstrumentPriceRepository extends JpaRepository<InstrumentPrice, Long> {

    @Query(value = """
        select *
        from instrument_price
                 join instrument i on i.id = instrument_price.instrument_id
                 join exchange e on e.id = i.exchange_id
        where i.ticker = :symbol
          and e.code = :exchange
        order by datetime desc
        limit 1
        """, nativeQuery = true)
    Optional<InstrumentPrice> findTopByTickerOrderByDatetimeDesc(String symbol, String exchange);
}
