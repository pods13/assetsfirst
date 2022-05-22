package com.topably.assets.portfolios.domain.dto;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDashboardDto {

    private Long id;
    private Set<DashboardCard> cards;
}
