package com.topably.assets.portfolios.domain.position;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.tag.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Accessors(chain = true)
public class PortfolioPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk__portfolio_position__portfolio_id__portfolio"))
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk__portfolio_position__instrument_id__instrument"))
    private Instrument instrument;

    @Column(name = "quantity", precision = 12, scale = 0)
    private BigInteger quantity;

    @Column(name = "average_price", precision = 20, scale = 4)
    private BigDecimal averagePrice;

    public BigDecimal getTotal() {
        return getAveragePrice().multiply(new BigDecimal(getQuantity()));
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "portfolio_position_tag",
        joinColumns = {@JoinColumn(name = "position_id", referencedColumnName = "id")},
        foreignKey = @ForeignKey(name = "fk__portfolio_position__position_id__portfolio_position"),
        inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")},
        inverseForeignKey = @ForeignKey(name = "fk__portfolio_position__tag_id__tag"))
    private Set<Tag> tags;

    private LocalDate openDate;

    private BigDecimal realizedPnl;
}
