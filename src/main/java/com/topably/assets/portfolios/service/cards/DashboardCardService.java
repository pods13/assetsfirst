package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.repository.PortfolioDashboardRepository;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardCardService {

    private final PortfolioDashboardRepository portfolioDashboardRepository;

    private final CardStateProducerFactory cardStateProducerFactory;
    private final PortfolioService portfolioService;

    public void addCard(Long dashboardId, DashboardCard card) {
        PortfolioDashboard dashboard = portfolioDashboardRepository.getById(dashboardId);
        dashboard.getCards().add(card);
        portfolioDashboardRepository.save(dashboard);
    }

    public void updateCards(Long dashboardId, Collection<DashboardCard> cardsNewState) {
        var dashboard = portfolioDashboardRepository.getById(dashboardId);
        dashboard.getCards().clear();
        dashboard.getCards().addAll(cardsNewState);
    }

    public void deleteCard(Long dashboardId, DashboardCard cardToDelete) {
        PortfolioDashboard dashboard = portfolioDashboardRepository.getById(dashboardId);
        Set<DashboardCard> cards = dashboard.getCards().stream()
            .filter(card -> !card.getId().equals(cardToDelete.getId())).collect(Collectors.toSet());
        dashboard.setCards(cards);
        portfolioDashboardRepository.save(dashboard);
    }

    public CardData produceCardData(Long userId, DashboardCard card) {
        Portfolio portfolio = portfolioService.findByUserId(userId);
        return cardStateProducerFactory.getProducer(card).produce(portfolio, card);
    }
}
