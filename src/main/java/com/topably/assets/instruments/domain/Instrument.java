package com.topably.assets.instruments.domain;


import java.util.Currency;
import java.util.Map;

import com.topably.assets.core.domain.Ticker;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "INSTRUMENT_TYPE")
@Table(
    name = "instrument", uniqueConstraints = {
    @UniqueConstraint(name = "instrument_symbol_exchange_code_key", columnNames = {"SYMBOL", "EXCHANGE_CODE"}),
}
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native")
    private Long id;

    @Column(name = "INSTRUMENT_TYPE", insertable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String instrumentType;

    @Column(name = "EXCHANGE_CODE")
    @EqualsAndHashCode.Include
    private String exchangeCode;

    @Column(name = "SYMBOL")
    @EqualsAndHashCode.Include
    private String symbol;

    @Column(name = "NAME")
    private String name;

    @Column(columnDefinition = "char(3)")
    private Currency currency;

    @Singular
    @Column(name = "ATTRIBUTES", columnDefinition = "json")
    @Type(JsonType.class)
    private Map<String, String> attributes;

    public Ticker toTicker() {
        return new Ticker(symbol, exchangeCode);
    }

}
