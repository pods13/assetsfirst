package com.topably.assets.instruments.domain;


import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.Exchange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.type.SqlTypes;

import java.util.Currency;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INSTRUMENT_TYPE")
@Table(name = "instrument", uniqueConstraints = {
    @UniqueConstraint(name = "instrument_ticker_exchange_id_key", columnNames = {"TICKER", "EXCHANGE_ID"}),
})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "INSTRUMENT_TYPE", insertable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String instrumentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXCHANGE_ID", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__instrument__exchange_id__exchange"))
    @EqualsAndHashCode.Include
    private Exchange exchange;

    @Column(name = "TICKER")
    @EqualsAndHashCode.Include
    private String ticker;

    @Column(columnDefinition = "char(3)")
    private Currency currency;

    @Singular
    @Column(name = "ATTRIBUTES", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attributes;

    public Ticker toTicker() {
        return new Ticker(ticker, exchange.getCode());
    }
}
