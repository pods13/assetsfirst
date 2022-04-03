package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import com.topably.assets.portfolios.service.cards.CardStateProducerFactory;
import com.topably.assets.portfolios.service.cards.PortfolioCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PortfolioCardsWSController {

    private final CardStateProducerFactory cardStateProducerFactory;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PortfolioCardService portfolioCardService;

    @MessageMapping("/cards")
    public void broadcastCardData(Principal user, @Payload PortfolioCard card) {
        this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/topic/cards/" + card.getId(),
                cardStateProducerFactory.getProducer(card).produce(user, card));
    }

    @MessageMapping("/{portfolioId}/cards/add")
    public void addCard(@DestinationVariable Long portfolioId, @Payload PortfolioCard card) {
        portfolioCardService.addCard(portfolioId, card);
    }

    @MessageMapping("/{portfolioId}/cards/update")
    public void updateCard(@DestinationVariable Long portfolioId, @Payload PortfolioCard card) {
        portfolioCardService.updateCard(portfolioId, card);
    }

    @MessageMapping("/{portfolioId}/cards/delete")
    public void deleteCard(@DestinationVariable Long portfolioId, @Payload PortfolioCard card) {
        portfolioCardService.deleteCard(portfolioId, card);
    }

}
