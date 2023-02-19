package com.topably.assets.core.bootstrap;

import com.topably.assets.findata.exchanges.domain.Exchange;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.findata.exchanges.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;

import static com.topably.assets.findata.exchanges.domain.USExchange.NASDAQ;
import static com.topably.assets.findata.exchanges.domain.USExchange.NYSE;
import static com.topably.assets.findata.exchanges.domain.USExchange.NYSEARCA;

@RequiredArgsConstructor
@Component
@Order(15)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class ExchangeDataLoader implements CommandLineRunner {

    private final ExchangeRepository exchangeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var exchanges = new ArrayList<Exchange>();
        exchanges.add(Exchange.builder()
            .name("New York Stock Exchange")
            .code(NYSE.name())
            .countryCode("US")
            .currency(Currency.getInstance("USD"))
            .build());
        exchanges.add(Exchange.builder()
            .name("Nasdaq Stock Market")
            .code(NASDAQ.name())
            .countryCode("US")
            .currency(Currency.getInstance("USD"))
            .build());
        exchanges.add(Exchange.builder()
            .name("NYSE Arca")
            .code(NYSEARCA.name())
            .countryCode("US")
            .currency(Currency.getInstance("USD"))
            .build());
        exchanges.add(Exchange.builder()
            .name("Moscow Exchange")
            .code(ExchangeEnum.MCX.name())
            .countryCode("RU")
            .currency(Currency.getInstance("RUB"))
            .build());
        exchanges.add(Exchange.builder()
            .name("Deutsche Börse")
            .code(ExchangeEnum.XETRA.name())
            .countryCode("DE")
            .currency(Currency.getInstance("EUR"))
            .build());
        exchanges.add(Exchange.builder()
            .name("The Stock Exchange of Hong Kong Limited")
            .code("HK")
            .countryCode("HK")
            .currency(Currency.getInstance("HKD"))
            .build());
        exchanges.add(Exchange.builder()
            .name("Forex IDC")
            .code(ExchangeEnum.FX_IDC.name())
            .countryCode("GLOBE")
            .currency(Currency.getInstance("USD"))
            .build());
        exchangeRepository.saveAll(exchanges);
    }
}
