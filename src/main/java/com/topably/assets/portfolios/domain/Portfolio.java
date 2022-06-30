package com.topably.assets.portfolios.domain;

import com.topably.assets.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "portfolio", uniqueConstraints = {
        @UniqueConstraint(name="uq_portfolio_user_id", columnNames = {"user_id"}),
        @UniqueConstraint(name="uq_portfolio_dashboard_id", columnNames = {"dashboard_id"}),
})
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__portfolio__user_id__user"))
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "dashboard_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk__portfolio__dashboard_id__portfolio_dashboard"))
    private PortfolioDashboard dashboard;
}
