package com.topably.assets.portfolios;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.service.cards.CardStateProducerFactory;
import com.topably.assets.portfolios.service.cards.DashboardCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DashboardCardsWSController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DashboardCardService dashboardCardService;

    @MessageMapping("/cards")
    public void broadcastCardData(Principal user, @Payload DashboardCard card) {
        this.simpMessagingTemplate.convertAndSendToUser(user.getName(), "/topic/cards/" + card.getId(),
                dashboardCardService.produceCardData(user.getName(), card));
    }

    @MessageMapping("/{dashboardId}/cards/add")
    public void addCard(@DestinationVariable Long dashboardId, @Payload DashboardCard card) {
        dashboardCardService.addCard(dashboardId, card);
    }

    @MessageMapping("/{dashboardId}/cards/update")
    public void updateCard(@DestinationVariable Long dashboardId, @Payload DashboardCard card) {
        dashboardCardService.updateCard(dashboardId, card);
    }

    @MessageMapping("/{dashboardId}/cards/delete")
    public void deleteCard(@DestinationVariable Long dashboardId, @Payload DashboardCard card) {
        dashboardCardService.deleteCard(dashboardId, card);
    }

}
