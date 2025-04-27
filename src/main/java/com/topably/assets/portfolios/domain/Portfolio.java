package com.topably.assets.portfolios.domain;

import com.topably.assets.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
