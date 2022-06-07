package com.topably.assets.core.bootstrap;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.repository.UserRepository;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.broker.BrokerRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Order(30)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class TradeDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BrokerRepository brokerRepository;

    private final TradeService tradeService;
    private final InstrumentService instrumentService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Long userId = userRepository.findByUsername("user").map(User::getId).orElse(null);
        var brokerNameById = brokerRepository.findAll().stream()
                .collect(Collectors.toMap(b -> b.getName().split(" ")[0], Broker::getId));
        addNewmont(userId, brokerNameById.get("Interactive"));
        addKRBN(userId, brokerNameById.get("Interactive"));
        addCNOOC(userId, brokerNameById.get("Interactive"));

        addBayer(userId, brokerNameById.get("Tinkoff"));
        addRosneft(userId, brokerNameById);

        addAltria(userId, brokerNameById.get("VTB"));
        addOmega(userId, brokerNameById.get("VTB"));
        addCoke(userId, brokerNameById.get("VTB"));
        addTotal(userId, brokerNameById.get("VTB"));
        addGazprom(userId, brokerNameById.get("Alfa"));
        addRosAgro(userId, brokerNameById.get("Finam"));
        addPolyus(userId, brokerNameById.get("Alfa"));
        addPhor(userId, brokerNameById.get("Alfa"));
        addIRao(userId, brokerNameById.get("Alfa"));
        addMts(userId, brokerNameById.get("Alfa"));
        addMagnit(userId, brokerNameById.get("Alfa"));
        addMoex(userId, brokerNameById.get("Alfa"));
        addFXCN(userId, brokerNameById.get("Finam"));
    }

    private void addNewmont(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 2, 23, 11, 0))
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(67.92))
                .quantity(BigInteger.valueOf(15L))
                .userId(userId)
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("NEM", USExchange.NYSE.name()));
    }

    private void addKRBN(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(40.67))
                .quantity(BigInteger.valueOf(24L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("KRBN", USExchange.NYSEARCA.name()));

        AddTradeDto dto2 = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 6, 3, 20, 33))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(50.33))
                .quantity(BigInteger.valueOf(24L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto2, instrumentService.findInstrument("KRBN", USExchange.NYSEARCA.name()));
    }

    private void addBayer(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(47.23))
                .quantity(BigInteger.valueOf(110L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("BAYN", "XETRA"));
    }

    private void addAltria(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(45.67))
                .quantity(BigInteger.valueOf(64L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("MO", USExchange.NYSE.name()));
    }

    private void addOmega(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(31.82))
                .quantity(BigInteger.valueOf(75L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("OHI", USExchange.NYSE.name()));
    }

    private void addCoke(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(52.04))
                .quantity(BigInteger.valueOf(24L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("KO", USExchange.NYSE.name()));
    }

    private void addTotal(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(45.3659))
                .quantity(BigInteger.valueOf(64L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("TTE", USExchange.NYSE.name()));
    }

    private void addRosneft(Long userId, Map<String, Long> brokerNameById) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 1, 18, 8, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(393.15))
                .quantity(BigInteger.valueOf(700L))
                .brokerId(brokerNameById.get("Tinkoff"))
                .build();
        Instrument rosn = instrumentService.findInstrument("ROSN", "MCX");
        tradeService.addTrade(dto, rosn);

        AddTradeDto dto2 = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 3, 24, 8, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(357.12))
                .quantity(BigInteger.valueOf(250L))
                .brokerId(brokerNameById.get("BCS"))
                .build();
        tradeService.addTrade(dto2, rosn);
    }

    private void addGazprom(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(288.96))
                .quantity(BigInteger.valueOf(900L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("GAZP", "MCX"));
    }

    private void addRosAgro(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(1029.8))
                .quantity(BigInteger.valueOf(440L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("AGRO", "MCX"));
    }

    private void addPolyus(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(13179.4))
                .quantity(BigInteger.valueOf(27L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto, instrumentService.findInstrument("PLZL", "MCX"));
    }

    private void addPhor(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(4945))
                .quantity(BigInteger.valueOf(10L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("PHOR", "MCX");
        tradeService.addTrade(dto, instrument);

        AddTradeDto dto2 = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 3, 24, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(4945))
                .quantity(BigInteger.valueOf(5L))
                .brokerId(brokerId)
                .build();
        tradeService.addTrade(dto2, instrument);
    }

    private void addIRao(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(5.0253))
                .quantity(BigInteger.valueOf(10000L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("IRAO", "MCX");
        tradeService.addTrade(dto, instrument);
    }

    private void addMts(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(317.74))
                .quantity(BigInteger.valueOf(250L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("MTSS", "MCX");
        tradeService.addTrade(dto, instrument);
    }

    private void addMagnit(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(5222.2))
                .quantity(BigInteger.valueOf(16L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("MGNT", "MCX");
        tradeService.addTrade(dto, instrument);
    }

    private void addMoex(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(135.51))
                .quantity(BigInteger.valueOf(110L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("MOEX", "MCX");
        tradeService.addTrade(dto, instrument);
    }

    private void addFXCN(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(3726))
                .quantity(BigInteger.valueOf(44L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("FXCN", "MCX");
        tradeService.addTrade(dto, instrument);
    }

    private void addCNOOC(Long userId, Long brokerId) {
        AddTradeDto dto = AddTradeDto.builder()
                .date(LocalDateTime.of(2022, 6, 2, 11, 0))
                .userId(userId)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(12.04))
                .quantity(BigInteger.valueOf(1000L))
                .brokerId(brokerId)
                .build();
        Instrument instrument = instrumentService.findInstrument("0883", "HK");
        tradeService.addTrade(dto, instrument);
    }
}
