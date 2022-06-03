package com.topably.assets.core.bootstrap;

import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;

import static com.topably.assets.exchanges.domain.USExchange.NYSE;
import static com.topably.assets.exchanges.domain.USExchange.NYSEARCA;

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
        brokers.add(Broker.builder()
                .name("Interactive Brokers LLC")
                .build());
        brokers.add(Broker.builder()
                .name("Tinkoff Investments")
                .build());
        brokers.add(Broker.builder()
                .name("VTB Investments")
                .build());
        brokers.add(Broker.builder()
                .name("Finam")
                .build());
        brokers.add(Broker.builder()
                .name("Alfa Direct")
                .build());
        brokers.add(Broker.builder()
                .name("BCS Investments")
                .build());
        brokerRepository.saveAll(brokers);
    }
}
