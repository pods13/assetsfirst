package com.topably.assets.core.bootstrap;

import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
@Order(14)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class BrokerDataLoader implements CommandLineRunner {

    private final BrokerRepository brokerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var brokers = new ArrayList<Broker>();
        brokers.add(new Broker().setName("Interactive Brokers LLC"));
        brokers.add(new Broker().setName("Tinkoff Investments"));
        brokers.add(new Broker().setName("VTB Investments"));
        brokers.add(new Broker().setName("Finam"));
        brokers.add(new Broker().setName("Alfa Direct"));
        brokers.add(new Broker().setName("BCS Investments"));
        brokerRepository.saveAll(brokers);
    }
}
