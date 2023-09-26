package com.topably.assets.instruments.repository.instrument;


import com.topably.assets.instruments.domain.instrument.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @EntityGraph(attributePaths = {"exchange"})
    Page<Stock> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"exchange", "company", "company.industry", "company.industry.sector"})
    List<Stock> findAllById(Iterable<Long> ids);

    @EntityGraph(attributePaths = {"exchange"})
    Optional<Stock> findBySymbolAndExchange_Code(String symbol, String exchangeCode);
}
