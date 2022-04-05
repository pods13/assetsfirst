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
        "       s2.security_type as trade_category,\n" +
        "       u2.username,\n" +
        "       s2.ticker,\n" +
        "       CASE\n" +
        "           WHEN s2.security_type = 'ETF' THEN s2.name\n" +
        "           WHEN s2.security_type = 'STOCK' THEN c2.name\n" +
        "           END          as name,\n" +
        "       trade.operation,\n" +
        "       trade.date,\n" +
        "       trade.quantity,\n" +
        "       trade.price,\n" +
        "       exch.currency\n" +
        "from security_trade trade\n" +
        "         join security s2 on s2.id = trade.security_id\n" +
        "         join user u2 on u2.id = trade.user_id\n" +
        "         join exchange exch on exch.id = s2.exchange_id\n" +
        "         left join company c2 on c2.id = s2.company_id\n" +
        "UNION ALL\n" +
        "select mt.id,\n" +
        "       'MONEY' as trade_category,\n" +
        "       u.username,\n" +
        "       null,\n" +
        "       mt.currency,\n" +
        "       mt.operation,\n" +
        "       mt.date,\n" +
        "       1       as quantity,\n" +
        "       mt.amount,\n" +
        "       mt.currency\n" +
        "from money_trade mt\n" +
        "         join user u on u.id = mt.user_id\n")
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
