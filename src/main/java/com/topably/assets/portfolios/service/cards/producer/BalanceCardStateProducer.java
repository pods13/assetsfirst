package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.output.BalanceCardData;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.PortfolioValuationService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service(CardContainerType.BALANCE)
@RequiredArgsConstructor
@Slf4j
public class BalanceCardStateProducer implements CardStateProducer<BalanceCard> {

    private final PortfolioService portfolioService;
    private final PortfolioValuationService portfolioValuationService;

    @Override
    public CardData produce(Portfolio portfolio, BalanceCard card) {
        var nextDay = LocalDate.now().plusDays(1L);

        return new BalanceCardData()
                .setInvestedAmount(portfolioValuationService.calculateInvestedAmountByDate(portfolio, nextDay))
                .setCurrentAmount(portfolioValuationService.calculateMarketValueByDate(portfolio, LocalDate.now()))
                .setCurrencyCode(portfolio.getCurrency().getCurrencyCode())
                .setInvestedValueByDates(portfolioService.getInvestedValueByDates(portfolio, 5));
    }
}
