package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.output.BalanceCardData;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;

@Service(CardContainerType.BALANCE)
@RequiredArgsConstructor
public class BalanceCardStateProducer implements CardStateProducer<BalanceCard> {

    private final PortfolioService portfolioService;

    @Override
    public CardData produce(Portfolio portfolio, BalanceCard card) {
        return BalanceCardData.builder()
            .investedAmount(portfolioService.calculateInvestedAmount(portfolio))
            .currentAmount(portfolioService.calculateCurrentAmount(portfolio))
            //TODO use portfolio currency instead
            .currencySymbol(Currency.getInstance("RUB").getSymbol())
            .build();
    }
}
