package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.DashboardCard;

public interface DashboardCardService {

    void addCard(Long dashboardId, DashboardCard card);

    void updateCard(Long dashboardId, DashboardCard cardToUpdate);

    void deleteCard(Long dashboardId, DashboardCard cardToDelete);

    CardData produceCardData(String username, DashboardCard card);
}
