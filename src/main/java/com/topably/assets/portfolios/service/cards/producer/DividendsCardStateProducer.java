package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.DividendsCard;
import com.topably.assets.portfolios.domain.cards.output.DividendsCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;

@Service(CardContainerType.DIVIDENDS)
@RequiredArgsConstructor
public class DividendsCardStateProducer implements CardStateProducer<DividendsCard> {

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendsCard card) {
        return DividendsCardData.builder()
                .dividends(new ArrayList<>())
                .build();
    }
}
