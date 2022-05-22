package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;

import java.security.Principal;

public interface CardStateProducer<T extends DashboardCard> {

    PortfolioCardData produce(Principal user, T card);

}
