package com.topably.assets.securities.repository.security;


import com.topably.assets.securities.domain.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @EntityGraph(attributePaths = {"exchange"})
    Page<Stock> findAll(Pageable pageable);
}
