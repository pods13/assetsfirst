package com.topably.assets.trades.listener;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.trades.service.TradeTrialDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TradeUserCreatedListener {

    private final TradeTrialDataProvider dataProvider;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApplicationEvent(UserCreatedEvent event) {
        if (event.isProvideData()) {
            dataProvider.provideData(event.getUserId());
        }
    }
}
