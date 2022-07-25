package com.topably.assets.instruments.repository;

import com.topably.assets.instruments.domain.Instrument;
import liquibase.pro.packaged.T;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

public interface InstrumentRepository extends JpaRepository<Instrument, Long>, JpaSpecificationExecutor<Instrument> {

    Instrument findByTickerAndExchange_Code(String ticker, String exchange);

}
