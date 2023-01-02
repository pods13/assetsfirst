package com.topably.assets.trades.repository;

import com.topably.assets.trades.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query(value = """
        select t.*, i.*, e.*
        from trade t
                 join portfolio_position pos on pos.id = t.portfolio_position_id
                 join portfolio p on p.id = pos.portfolio_id and p.id = :portfolioId
                 join instrument i on i.id = pos.instrument_id
                 join exchange e on e.id = i.exchange_id
        where exists(select d.id
                     from dividend d
                              join instrument i2 on i2.id = d.instrument_id
                     where i2.id = i.id
                       and ((d.pay_date is not null and year(d.pay_date) in :dividendYears)
                         or (d.pay_date is null and year(adddate(d.record_date, interval 1 month)) in :dividendYears))
                  )
        order by t.date
        """, nativeQuery = true)
    Collection<Trade> findDividendPayingTradesOrderByTradeDate(Long portfolioId, Collection<Integer> dividendYears);

    @Query(value = """
        select t from Trade t
        join fetch t.portfolioPosition pos
        join fetch pos.instrument i
        join fetch i.exchange exch
        join fetch t.broker br
        join pos.portfolio p
        join p.user u
        where u.id = :userId
        """)
    Collection<Trade> findAllByUserId(Long userId);
}
