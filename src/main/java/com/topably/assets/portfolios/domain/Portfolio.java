package com.topably.assets.portfolios.domain;

import com.topably.assets.auth.domain.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "portfolio", uniqueConstraints = {
    @UniqueConstraint(name = "uq_portfolio_user_id", columnNames = {"user_id"}),
    @UniqueConstraint(name = "uq_portfolio_dashboard_id", columnNames = {"dashboard_id"}),
})
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__portfolio__user_id__user"))
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "dashboard_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk__portfolio__dashboard_id__portfolio_dashboard"))
    private PortfolioDashboard dashboard;

    @Column(columnDefinition = "char(3)")
    private Currency currency;
}
