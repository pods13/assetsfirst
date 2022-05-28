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

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardCardServiceImpl implements DashboardCardService {

    private final PortfolioDashboardRepository portfolioDashboardRepository;

    private final CardStateProducerFactory cardStateProducerFactory;
    private final PortfolioService portfolioService;

    @Override
    @Transactional
    public void addCard(Long dashboardId, DashboardCard card) {
        PortfolioDashboard dashboard = portfolioDashboardRepository.getById(dashboardId);
        dashboard.getCards().add(card);
        portfolioDashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public void updateCard(Long dashboardId, DashboardCard cardToUpdate) {
        PortfolioDashboard dashboard = portfolioDashboardRepository.getById(dashboardId);
        Set<DashboardCard> cards = dashboard.getCards().stream()
                .filter(card -> !card.getId().equals(cardToUpdate.getId())).collect(Collectors.toSet());
        cards.add(cardToUpdate);
        dashboard.setCards(cards);
        portfolioDashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public void deleteCard(Long dashboardId, DashboardCard cardToDelete) {
        PortfolioDashboard dashboard = portfolioDashboardRepository.getById(dashboardId);
        Set<DashboardCard> cards = dashboard.getCards().stream()
                .filter(card -> !card.getId().equals(cardToDelete.getId())).collect(Collectors.toSet());
        dashboard.setCards(cards);
        portfolioDashboardRepository.save(dashboard);
    }

    @Override
    @Transactional
    public CardData produceCardData(String username, DashboardCard card) {
        Portfolio portfolio = portfolioService.findByUsername(username);
        return cardStateProducerFactory.getProducer(card).produce(portfolio, card);
    }
}
