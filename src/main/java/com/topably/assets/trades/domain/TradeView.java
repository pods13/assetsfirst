package com.topably.assets.trades.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Currency;

@Setter
@Getter
@Entity
@Immutable
@Subselect("select trade.id,\n" +
        "       s2.instrument_type as trade_category,\n" +
        "       u2.username,\n" +
        "       s2.ticker,\n" +
        "       CASE\n" +
        "           WHEN s2.instrument_type = 'ETF' THEN s2.attributes ->> \"$.name\"\n" +
        "           WHEN s2.instrument_type = 'STOCK' THEN c2.name\n" +
        "           END            as name,\n" +
        "       trade.operation,\n" +
        "       trade.date,\n" +
        "       trade.quantity,\n" +
        "       trade.price,\n" +
        "       exch.currency\n" +
        "from trade\n" +
        "         join portfolio_holding ph on trade.portfolio_holding_id = ph.id\n" +
        "         join portfolio p on p.id = ph.portfolio_id\n" +
        "         join user u2 on u2.id = p.user_id\n" +
        "         join instrument s2 on ph.instrument_id = s2." +
        "id\n" +
        "         join exchange exch on exch.id = s2.exchange_id\n" +
        "         left join company c2 on c2.id = s2.company_id\n")
@IdClass(TradeViewId.class)
public class TradeView {

    @Id
    private Long id;
    @Id
    private String tradeCategory;
    @Id
    private String username;

    private String ticker;

    private String name;

    private LocalDateTime date;

    @Enumerated
    @Column(columnDefinition = "tinyint")
    private TradeOperation operation;

    private BigInteger quantity;
    private BigDecimal price;

    private Currency currency;

}
