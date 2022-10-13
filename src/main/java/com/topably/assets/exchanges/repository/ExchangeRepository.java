package com.topably.assets.exchanges.repository;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.exchanges.domain.Exchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Exchange findByCode(String code);

    @Query(value = """
            select new com.topably.assets.core.domain.Ticker(i.ticker, e.code) from Exchange e
            join Instrument i on i.exchange.id = e.id and i.instrumentType in :instrumentTypes
            where e.id in (select exch.id from Exchange exch where :exchangeCodes is null or exch.code in :exchangeCodes)
            """)
    Page<Ticker> findInstrumentsOfCertainTypesByExchangeCodes(Pageable pageable,
                                                              Collection<String> exchangeCodes,
                                                              Collection<String> instrumentTypes);
}
