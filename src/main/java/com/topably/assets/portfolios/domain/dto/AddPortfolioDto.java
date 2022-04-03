package com.topably.assets.portfolios.domain.dto;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddPortfolioDto {

    @Builder.Default
    private Set<PortfolioCard> cards = new HashSet<>();
}
