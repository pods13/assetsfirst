package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.service.cards.DashboardCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class DashboardCardsWSController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DashboardCardService dashboardCardService;

    @MessageMapping("/cards")
    public void broadcastCardData(@AuthenticationPrincipal CurrentUser user, @Payload DashboardCard card) {
        this.simpMessagingTemplate.convertAndSendToUser(user.getUsername(), "/topic/cards/" + card.getId(),
                dashboardCardService.produceCardData(user.getUserId(), card));
    }

    @MessageMapping("/{dashboardId}/cards/add")
    public void addCard(@DestinationVariable Long dashboardId, @Payload DashboardCard card) {
        dashboardCardService.addCard(dashboardId, card);
    }

    @MessageMapping("/{dashboardId}/cards/update")
    public void updateCards(@DestinationVariable Long dashboardId, @Payload Collection<DashboardCard> cards) {
        dashboardCardService.updateCards(dashboardId, cards);
    }

    @MessageMapping("/{dashboardId}/cards/delete")
    public void deleteCard(@DestinationVariable Long dashboardId, @Payload DashboardCard card) {
        dashboardCardService.deleteCard(dashboardId, card);
    }

}
