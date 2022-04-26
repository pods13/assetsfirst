package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service(CardContainerType.SECTORAL_DISTRIBUTION)
@RequiredArgsConstructor
public class SectoralDistributionCardStateProducer implements CardStateProducer<SectoralDistributionCard> {

    @Override
    public PortfolioCardData produce(Principal user, SectoralDistributionCard card) {
        return SectoralDistributionCardData.builder()
                .test("test")
                .build();
    }
}
