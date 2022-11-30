package com.topably.assets.instruments.repository;

import com.topably.assets.instruments.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstrumentRepository extends JpaRepository<Instrument, Long>, JpaSpecificationExecutor<Instrument> {

    Instrument findByTickerAndExchange_Code(String ticker, String exchange);

}
