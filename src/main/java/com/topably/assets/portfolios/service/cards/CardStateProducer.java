package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.CardData;

import java.security.Principal;

public interface CardStateProducer<T extends DashboardCard> {

    CardData produce(Principal user, T card);

}
