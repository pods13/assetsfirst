package com.topably.assets.trades.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Subselect("""
        select trade.id,
               s2.id as instrument_id,
               s2.instrument_type,
               u2.username,
               s2.ticker,
               CASE
                   WHEN s2.instrument_type = 'ETF' THEN s2.attributes ->> "$.name"
                   WHEN s2.instrument_type = 'STOCK' THEN c2.name
                   END            as name,
               trade.operation,
               trade.date,
               trade.quantity,
               trade.price,
               exch.currency
        from trade
                 join portfolio_holding ph on trade.portfolio_holding_id = ph.id
                 join portfolio p on p.id = ph.portfolio_id
                 join user u2 on u2.id = p.user_id
                 join instrument s2 on ph.instrument_id = s2.id
                 join exchange exch on exch.id = s2.exchange_id
                 left join company c2 on c2.id = s2.company_id
        """)
public class TradeView {

    @Id
    private Long id;

    private Long instrumentId;
    private String instrumentType;

    @JsonIgnore
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
