package com.topably.assets.instruments.repository;

import com.topably.assets.instruments.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    Collection<Instrument> findAllByTickerLikeAndInstrumentTypeIn(String search, Collection<String> instrumentTypes);

    Collection<Instrument> findAllByInstrumentTypeInAndExchange_CodeIn(Collection<String> instrumentTypes,
                                                                       Collection<String> exchangeCodes);

    Instrument findByTickerAndExchange_Code(String ticker, String exchange);

}
