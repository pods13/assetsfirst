package com.topably.assets.portfolios.listener;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.portfolios.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PortfolioUserCreatedListener {

    private final PortfolioService portfolioService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApplicationEvent(UserCreatedEvent event) {
        portfolioService.createDefaultUserPortfolio(event.getUserId(), event.isProvideData());
    }
}
