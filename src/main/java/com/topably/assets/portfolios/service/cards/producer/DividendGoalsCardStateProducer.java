package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.DividendGoalsCard;
import com.topably.assets.portfolios.domain.cards.input.DividendsCard;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service(CardContainerType.DIVIDEND_GOALS)
@RequiredArgsConstructor
public class DividendGoalsCardStateProducer implements CardStateProducer<DividendGoalsCard> {

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendGoalsCard card) {
        return null;
    }
}
