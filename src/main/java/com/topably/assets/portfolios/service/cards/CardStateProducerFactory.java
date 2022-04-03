package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CardStateProducerFactory {

    private final Map<String, CardStateProducer<?>> cardStateProducers;

    public CardStateProducerFactory(Map<String, CardStateProducer<?>> cardStateProducers) {
        this.cardStateProducers = cardStateProducers;
    }

    @SuppressWarnings("unchecked")
    public <T extends PortfolioCard> CardStateProducer<T> getProducer(T card) {
        return (CardStateProducer<T>) cardStateProducers.get(card.getContainerType());
    }
}
