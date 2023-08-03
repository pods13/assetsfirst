package com.topably.assets.portfolios.listener;

import com.topably.assets.auth.event.UserCreatedEvent;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.portfolios.service.cards.DashboardCardTrialDataProvider;
import com.topably.assets.portfolios.service.tag.TagCategoryTrialDataProvider;
import com.topably.assets.trades.service.TradeTrialDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioUserCreatedListenerTest {

    private PortfolioRepository portfolioRepository;
    private UserService userService;
    private DashboardCardTrialDataProvider cardTrialDataProvider;
    private TradeTrialDataProvider tradeTrialDataProvider;
    private TagCategoryTrialDataProvider tagCategoryTrialDataProvider;

    private PortfolioUserCreatedListener listener;

    @BeforeEach
    public void beforeEach() {
        portfolioRepository = mock(PortfolioRepository.class);
        userService = mock(UserService.class);
        cardTrialDataProvider = mock(DashboardCardTrialDataProvider.class);
        tradeTrialDataProvider = mock(TradeTrialDataProvider.class);
        tagCategoryTrialDataProvider = mock(TagCategoryTrialDataProvider.class);

        listener = new PortfolioUserCreatedListener(portfolioRepository, userService, cardTrialDataProvider, tradeTrialDataProvider, tagCategoryTrialDataProvider);
    }

    @Test
    public void testUserCreatedWithDataProvided() {
        var userId = 1L;
        listener.onApplicationEvent(new UserCreatedEvent(this, userId, true));

        verify(cardTrialDataProvider, times(1)).provideCards();
        verify(tradeTrialDataProvider, times(1)).provideData(eq(userId));
        verify(tagCategoryTrialDataProvider, times(1)).provideData(eq(userId));
    }

    @Test
    public void testUserCreatedWithoutDataProvided() {
        var userId = 1L;
        listener.onApplicationEvent(new UserCreatedEvent(this, userId, false));

        verify(cardTrialDataProvider, times(0)).provideCards();
        verify(tradeTrialDataProvider, times(0)).provideData(eq(userId));
        verify(tagCategoryTrialDataProvider, times(1)).provideData(eq(userId));
    }

}
