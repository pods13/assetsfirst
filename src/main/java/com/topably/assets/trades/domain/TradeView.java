package com.topably.assets.trades.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Currency;

@Setter
@Getter
@Accessors(chain = true)
@Entity
@Immutable
@Subselect("""
    select trade.id,
           trade.portfolio_position_id as position_id,
           s2.id as instrument_id,
           s2.instrument_type,
           p.user_id,
           s2.ticker as symbol,
           exch.code as exchange,
           CASE
               WHEN s2.instrument_type = 'ETF' THEN s2.attributes ->> "$.name"
               WHEN s2.instrument_type = 'STOCK' THEN c2.name
               END            as name,
           trade.operation,
           trade.date,
           trade.quantity,
           trade.price,
           exch.currency,
           b.id as broker_id,
           b.name as broker_name
    from trade
             join portfolio_position pos on trade.portfolio_position_id = pos.id
             join portfolio p on p.id = pos.portfolio_id
             join instrument s2 on pos.instrument_id = s2.id
             join exchange exch on exch.id = s2.exchange_id
             left join company c2 on c2.id = s2.company_id
             join broker b on b.id = trade.broker_id
    """)
public class TradeView {

    @Id
    private Long id;
    @JsonIgnore
    private Long positionId;

    private Long instrumentId;
    private String instrumentType;

    @JsonIgnore
    private Long userId;

    private String symbol;
    private String exchange;

    private String name;

    private LocalDate date;

    @Enumerated
    @Column(columnDefinition = "tinyint")
    private TradeOperation operation;

    private BigInteger quantity;
    private BigDecimal price;

    private Currency currency;

    private Long brokerId;

    private String brokerName;

}
