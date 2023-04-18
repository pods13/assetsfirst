package com.topably.assets.portfolios.listener;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioDashboard;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.portfolios.service.cards.DashboardCardTrialDataProvider;
import com.topably.assets.portfolios.service.tag.TagCategoryTrialDataProvider;
import com.topably.assets.trades.service.TradeTrialDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class PortfolioUserCreatedListener {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final DashboardCardTrialDataProvider cardTrialDataProvider;
    private final TradeTrialDataProvider tradeTrialDataProvider;
    private final TagCategoryTrialDataProvider tagCategoryTrialDataProvider;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApplicationEvent(UserCreatedEvent event) {
        createDefaultUserPortfolio(event.getUserId(), event.isProvideData());
    }

    public void createDefaultUserPortfolio(Long userId, boolean provideData) {
        var dashboard = PortfolioDashboard.builder()
            .cards(provideData ? cardTrialDataProvider.provideCards() : new HashSet<>())
            .build();
        Portfolio portfolio = Portfolio.builder()
            .user(userService.getById(userId))
            .dashboard(dashboard)
            .build();
        portfolioRepository.save(portfolio);
        if (provideData) {
            tradeTrialDataProvider.provideData(userId);
        }
        tagCategoryTrialDataProvider.provideData(userId);
    }
}
