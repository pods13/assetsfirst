package com.topably.assets.core.bootstrap;

import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Order(30)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class TradeDataLoader implements CommandLineRunner {

    private final TradeRepository tradeRepository;

    private final TradeService tradeService;
    private final InstrumentService instrumentService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addNewmont();
        addKRBN();

        addBayer();
        addRosneft();

        addAltria();
        addOmega();
        addCoke();
        addTotal();
        addGazprom();
        addRosAgro();
        addPolyus();
        addPhor();
    }

    private void addNewmont() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 2, 23, 11, 0))
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(67.92))
                .quantity(BigInteger.valueOf(15L))
                .username("user")
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("NEM", USExchange.NYSE.name()));
    }

    private void addKRBN() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(40.67))
                .quantity(BigInteger.valueOf(24L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("KRBN", USExchange.NYSEARCA.name()));
    }

    private void addBayer() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(47.23))
                .quantity(BigInteger.valueOf(110L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("BAYN", "XETRA"));
    }

    private void addAltria() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(45.67))
                .quantity(BigInteger.valueOf(64L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("MO", USExchange.NYSE.name()));
    }

    private void addOmega() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(31.82))
                .quantity(BigInteger.valueOf(75L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("OHI", USExchange.NYSE.name()));
    }

    private void addCoke() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(52.04))
                .quantity(BigInteger.valueOf(24L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("KO", USExchange.NYSE.name()));
    }

    private void addTotal() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(45.3659))
                .quantity(BigInteger.valueOf(64L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("TTE", USExchange.NYSE.name()));
    }

    private void addRosneft() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 1, 18, 8, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(393.15))
                .quantity(BigInteger.valueOf(700L))
                .build();
        Instrument rosn = instrumentService.findInstrument("ROSN", "MCX");
        tradeService.addTrade(dto, rosn);

        AddTradeDto dto2 = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 3, 24, 8, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(357.12))
                .quantity(BigInteger.valueOf(250L))
                .build();
        tradeService.addTrade(dto2, rosn);
    }

    private void addGazprom() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(288.96))
                .quantity(BigInteger.valueOf(900L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("GAZP", "MCX"));
    }

    private void addRosAgro() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(1029.8))
                .quantity(BigInteger.valueOf(440L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("AGRO", "MCX"));
    }

    private void addPolyus() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(13179.4))
                .quantity(BigInteger.valueOf(27L))
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("PLZL", "MCX"));
    }

    private void addPhor() {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(4945))
                .quantity(BigInteger.valueOf(10L))
                .build();
        Instrument instrument = instrumentService.findInstrument("PHOR", "MCX");
        tradeService.addTrade(dto, instrument);

        AddTradeDto dto2 = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 3, 24, 11, 0))
                .username("user")
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(4945))
                .quantity(BigInteger.valueOf(5L))
                .build();
        tradeService.addTrade(dto2, instrument);
    }
}
