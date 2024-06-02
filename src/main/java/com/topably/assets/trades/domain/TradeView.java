package com.topably.assets.trades.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
           s2.symbol as symbol,
           s2.exchange_code as exchange,
           s2.name as name,
           trade.operation,
           trade.date,
           trade.quantity,
           trade.price,
           s2.currency,
           t.id as intermediary_id,
           t.name as intermediary_name
    from trade
             join portfolio_position pos on trade.portfolio_position_id = pos.id
             join portfolio p on p.id = pos.portfolio_id
             join instrument s2 on pos.instrument_id = s2.id
             join tag t on t.id = trade.intermediary_id
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

    public BigDecimal getTotal() {
        return price.multiply(new BigDecimal(quantity));
    }

    @JsonIgnore
    private Currency currency;

    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    private Long intermediaryId;

    private String intermediaryName;

}
