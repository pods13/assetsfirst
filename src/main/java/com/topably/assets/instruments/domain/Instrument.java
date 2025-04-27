package com.topably.assets.instruments.domain;


import com.topably.assets.core.domain.Ticker;
import com.topably.assets.tags.domain.Tag;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "instrument_tag",
            joinColumns = {@JoinColumn(name = "instrument_id", referencedColumnName = "id")},
            foreignKey = @ForeignKey(name = "fk__instrument__instrument_id__portfolio_position"),
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")},
            inverseForeignKey = @ForeignKey(name = "fk__portfolio_position__tag_id__tag")
    )
    private Set<Tag> tags;

    public Ticker toTicker() {
        return new Ticker(symbol, exchangeCode);
    }

    public void addTag(Tag tag) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        if (tag != null) {
            tags.add(tag);
        }
    }

}
