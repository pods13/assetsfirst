package com.topably.assets.securities.repository.security;


import com.topably.assets.securities.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
