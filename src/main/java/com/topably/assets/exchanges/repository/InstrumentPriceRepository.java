package com.topably.assets.exchanges.repository;

import com.topably.assets.exchanges.domain.InstrumentPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrumentPriceRepository extends JpaRepository<InstrumentPrice, Long> {

    Optional<InstrumentPrice> findTopBySymbolOrderByDatetimeDesc(String symbol);
}
