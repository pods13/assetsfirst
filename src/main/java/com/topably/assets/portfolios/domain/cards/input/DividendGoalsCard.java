package com.topably.assets.portfolios.domain.cards.input;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class DividendGoalsCard extends PortfolioCard {

    private Map<String, BigInteger> desiredPositionByIssuer;

}
