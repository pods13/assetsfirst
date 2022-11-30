package com.topably.assets.portfolios.domain;

import com.topably.assets.instruments.domain.Instrument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PortfolioHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__portfolio_holding__portfolio_id__portfolio"))
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__portfolio_holding__instrument_id__instrument"))
    private Instrument instrument;

    @Column(name = "quantity", precision = 12, scale = 0)
    private BigInteger quantity;

    @Column(name = "average_price", precision = 20, scale = 4)
    private BigDecimal averagePrice;

    public BigDecimal getTotal() {
        return getAveragePrice().multiply(new BigDecimal(getQuantity()));
    }
}
