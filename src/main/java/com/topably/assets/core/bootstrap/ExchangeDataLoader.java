package com.topably.assets.core.bootstrap;

import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;

@RequiredArgsConstructor
@Component
@Order(2)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class ExchangeDataLoader implements CommandLineRunner {

    private final ExchangeRepository exchangeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var exchanges = new ArrayList<Exchange>();
        exchanges.add(Exchange.builder()
                .name("New York Stock Exchange")
                .code("NYSE")
                .countryCode("US")
                .currency(Currency.getInstance("USD"))
                .build());
        exchanges.add(Exchange.builder()
                .name("NYSE Arca")
                .code("NYSEARCA")
                .countryCode("US")
                .currency(Currency.getInstance("USD"))
                .build());
        exchanges.add(Exchange.builder()
                .name("Moscow Exchange")
                .code("MCX")
                .countryCode("RU")
                .currency(Currency.getInstance("RUB"))
                .build());
        exchanges.add(Exchange.builder()
                .name("Deutsche Börse")
                .code("XETRA")
                .countryCode("DE")
                .currency(Currency.getInstance("EUR"))
                .build());
        exchangeRepository.saveAll(exchanges);
    }
}
