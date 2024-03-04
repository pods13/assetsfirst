package com.topably.assets.trades;

import java.util.Collection;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.trades.domain.dto.broker.BrokerDto;
import com.topably.assets.trades.service.broker.BrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/brokers")
@RequiredArgsConstructor
public class BrokerController {

    private final BrokerService brokerService;

    @GetMapping
    public Collection<BrokerDto> getBrokers(@AuthenticationPrincipal CurrentUser user) {
        return brokerService.getBrokers(user.getUserId());
    }

}
