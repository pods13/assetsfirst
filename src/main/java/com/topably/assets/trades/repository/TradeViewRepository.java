package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.TradeView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TradeViewRepository extends JpaRepository<TradeView, Long> {

    Page<TradeView> findByUserId(Long userId, Pageable pageable);

    List<TradeView> findAllByUserIdAndDateGreaterThanEqualOrderByDate(Long userId, LocalDate date);

    List<TradeView> findAllByPositionIdOrderByDate(Long positionId);
}
