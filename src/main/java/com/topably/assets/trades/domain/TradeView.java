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
@Subselect("select st.id,\n" +
        "       'STOCK' as trade_category,\n" +
        "       u.username,\n" +
        "       s.ticker,\n" +
        "       c.name,\n" +
        "       st.operation,\n" +
        "       st.date,\n" +
        "       st.quantity,\n" +
        "       st.price,\n" +
        "       s.currency\n" +
        "from stock_trade st\n" +
        "         join stock s on s.id = st.stock_id\n" +
        "         join company c on c.id = s.company_id\n" +
        "         join user u on u.id = st.user_id\n" +
        "UNION ALL\n" +
        "select et.id,\n" +
        "       'ETF' as trade_category,\n" +
        "    u.username,\n" +
        "       etf.ticker,\n" +
        "       etf.name,\n" +
        "       et.operation,\n" +
        "       et.date,\n" +
        "       et.quantity,\n" +
        "       et.price,\n" +
        "       etf.currency\n" +
        "from etf_trade et\n" +
        "         join etf on etf.id = et.etf_id\n" +
        "         join user u on u.id = et.user_id\n" +
        "UNION ALL\n" +
        "select mt.id,\n" +
        "       'MONEY' as trade_category,\n" +
        "       u.username,\n" +
        "       null,\n" +
        "       mt.currency,\n" +
        "       mt.operation,\n" +
        "       mt.date,\n" +
        "       1 as quantity,\n" +
        "       mt.amount,\n" +
        "       mt.currency\n" +
        "from money_trade mt\n" +
        "         join user u on u.id = mt.user_id")
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
