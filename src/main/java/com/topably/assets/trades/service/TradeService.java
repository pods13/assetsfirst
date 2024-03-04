package com.topably.assets.trades.service;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeService {

    private final TradeRepository tradeRepository;
    private final TradeViewRepository tradeViewRepository;

    public Collection<Trade> findDividendPayingTrades(Long portfolioId, Collection<Integer> dividendYears) {
        return tradeRepository.findDividendPayingTradesOrderByTradeDate(portfolioId, dividendYears);
    }

    public Page<TradeView> getUserTrades(Long userId, Pageable pageable) {
        return tradeViewRepository.findByUserId(userId, pageable);
    }

    public Collection<TradeView> getUserTradesForCurrentYear(Portfolio portfolio) {
        var firstDayOfCurrentYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        return tradeViewRepository.findAllByUserIdAndDateGreaterThanEqualOrderByDate(portfolio.getUser().getId(), firstDayOfCurrentYear);
    }
}
