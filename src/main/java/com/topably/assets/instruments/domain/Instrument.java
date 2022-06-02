package com.topably.assets.instruments.domain;


import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.core.domain.TickerSymbol;
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INSTRUMENT_TYPE")
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "INSTRUMENT_TYPE", insertable = false, updatable = false)
    private String instrumentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCHANGE_ID", referencedColumnName = "ID")
    private Exchange exchange;

    private String ticker;

    @Singular
    @Column(name = "ATTRIBUTES", columnDefinition = "json")
    @Type(type = "json")
    private Map<String, String> attributes;

    public TickerSymbol toTickerSymbol() {
        return new TickerSymbol(ticker, exchange.getCode());
    }
}
