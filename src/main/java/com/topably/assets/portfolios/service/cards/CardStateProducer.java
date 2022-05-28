package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.CardData;

public interface CardStateProducer<T extends DashboardCard> {

    CardData produce(Portfolio portfolio, T card);

}
