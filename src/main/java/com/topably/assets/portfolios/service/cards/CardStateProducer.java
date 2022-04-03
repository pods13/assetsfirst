package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;

import java.security.Principal;

public interface CardStateProducer<T extends PortfolioCard> {

    PortfolioCardData produce(Principal user, T card);

}
