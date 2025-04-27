package com.topably.assets.portfolios.domain;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PortfolioDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Singular
    @Column(name = "CARDS", columnDefinition = "json")
    @Type(JsonType.class)
    private Set<DashboardCard> cards;
}
