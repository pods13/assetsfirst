package com.topably.assets.trades;

import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.service.broker.BrokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/brokers")
@RequiredArgsConstructor
public class BrokerController {

    private final BrokerService brokerService;

    @GetMapping
    public Collection<Broker> getBrokers() {
        return brokerService.getBrokers();
    }
}
