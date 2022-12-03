package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.InvestmentYieldCard;
import com.topably.assets.portfolios.domain.cards.output.InvestmentYieldCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service(CardContainerType.INVESTMENT_YIELD)
@RequiredArgsConstructor
public class InvestmentYieldCardStateProducer implements CardStateProducer<InvestmentYieldCard> {

    @Override
    public CardData produce(Portfolio portfolio, InvestmentYieldCard card) {
        return new InvestmentYieldCardData()
            .setDividendYield(BigDecimal.ZERO);
    }
}
