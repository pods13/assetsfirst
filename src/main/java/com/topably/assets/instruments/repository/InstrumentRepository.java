package com.topably.assets.instruments.repository;

import java.util.Collection;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface InstrumentRepository extends JpaRepository<Instrument, Long>, JpaSpecificationExecutor<Instrument> {

    Instrument findBySymbolAndExchangeCode(String symbol, String exchange);

    @Query(
        value = """
                select new com.topably.assets.core.domain.Ticker(i.symbol, i.exchangeCode) from Instrument i
                    where i.instrumentType in (:instrumentTypes) and (:exchangeCodes is null or i.exchangeCode in (:exchangeCodes))
                    and (:inAnyPortfolio = false or i.id in (select distinct p.instrument.id from PortfolioPosition p))
                """
    )
    Page<Ticker> findInstrumentsOfCertainTypesByExchangeCodes(
        Pageable pageable,
        Collection<String> exchangeCodes,
        Collection<String> instrumentTypes, boolean inAnyPortfolio
    );

}
