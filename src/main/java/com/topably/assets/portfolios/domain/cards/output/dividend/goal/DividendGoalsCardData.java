package com.topably.assets.portfolios.domain.cards.output.dividend.goal;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividendGoalsCardData implements CardData {

    private Collection<PositionItem> items;
}
