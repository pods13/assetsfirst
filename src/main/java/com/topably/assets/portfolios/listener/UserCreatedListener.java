package com.topably.assets.portfolios.listener;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.portfolios.service.PortfolioDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final PortfolioDashboardService portfolioDashboardService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApplicationEvent(UserCreatedEvent event) {
        portfolioDashboardService.createDefaultUserPortfolioDashboard(event.getUserId());
    }
}
