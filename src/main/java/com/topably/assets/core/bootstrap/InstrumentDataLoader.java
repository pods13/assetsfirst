package com.topably.assets.core.bootstrap;

import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.repository.instrument.FXRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Order(4)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class InstrumentDataLoader implements CommandLineRunner {

    private final ExchangeRepository exchangeRepository;
    private final FXRepository fxRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Exchange mcx = exchangeRepository.findByCode("MCX");
        var usdrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("USDRUB_TOM")
                .build();
        var eurrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("EURRUB_TOM")
                .build();
        var gldrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("GLDRUB_TOM")
                .build();
        fxRepository.saveAll(List.of(usdrub_tom, eurrub_tom, gldrub_tom));
    }
}
