package com.topably.assets.trades.domain;

import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.trades.domain.broker.Broker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_position_id", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__trade__portfolio_position_id__portfolio_position"))
    private PortfolioPosition portfolioPosition;

    private LocalDate date;

    @Enumerated
    @Column(columnDefinition = "tinyint", nullable = false)
    private TradeOperation operation;

    @Column(name = "quantity", precision = 12, scale = 0)
    private BigInteger quantity;
    @Column(name = "price", precision = 20, scale = 4)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BROKER_ID", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "fk__trade__broker_id__broker"))
    private Broker broker;
}
