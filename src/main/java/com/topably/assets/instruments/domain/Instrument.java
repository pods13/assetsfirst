package com.topably.assets.instruments.domain;


import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.Exchange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "INSTRUMENT_TYPE", insertable = false, updatable = false)
    private String instrumentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXCHANGE_ID", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__instrument__exchange_id__exchange"))
    private Exchange exchange;

    @Column(name = "TICKER")
    private String ticker;

    @Column(columnDefinition = "char(3)")
    private Currency currency;

    @Singular
    @Column(name = "ATTRIBUTES", columnDefinition = "json")
    @Type(type = "json")
    private Map<String, String> attributes;

    public Ticker toTicker() {
        return new Ticker(ticker, exchange.getCode());
    }
}
