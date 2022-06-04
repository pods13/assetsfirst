package com.topably.assets.trades.service.broker;

import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BrokerService {

    private final BrokerRepository brokerRepository;


    public Collection<Broker> getBrokers() {
        return brokerRepository.findAll();
    }
}
