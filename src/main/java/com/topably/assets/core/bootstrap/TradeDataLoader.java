package com.topably.assets.core.bootstrap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.repository.UserRepository;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.findata.exchanges.domain.USExchange;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.broker.BrokerDto;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import com.topably.assets.trades.service.broker.BrokerService;
import com.topably.assets.trades.service.instrument.ETFTradeService;
import com.topably.assets.trades.service.instrument.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Order(30)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class TradeDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BrokerService brokerService;

    private final InstrumentService instrumentService;
    private final StockTradeService stockTradeService;
    private final ETFTradeService etfTradeService;

    @Override
    public void run(String... args) throws Exception {
        Long userId = userRepository.findByUsername("user").map(User::getId).orElse(null);
        var brokerNameById = brokerService.getBrokers(userId).stream()
            .collect(Collectors.toMap(b -> b.name().split(" ")[0], BrokerDto::id));
        addNewmont(userId, brokerNameById.get("Interactive"));
        addKRBN(userId, brokerNameById.get("Interactive"));
        addCNOOC(userId, brokerNameById.get("Interactive"));

        addBayer(userId, brokerNameById.get("Tinkoff"));
        addRosneft(userId, brokerNameById);

        addAltria(userId, brokerNameById);
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
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 2, 23))
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(67.92))
            .quantity(BigInteger.valueOf(15L))
            .userId(userId)
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("NEM", USExchange.NYSE.name()).getId())
            .build());
    }

    private void addKRBN(Long userId, Long brokerId) {
        Long etfId = instrumentService.findInstrument("KRBN", USExchange.NYSEARCA.name()).getId();
        etfTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(40.67))
            .quantity(BigInteger.valueOf(24L))
            .intermediaryId(brokerId)
            .instrumentId(etfId)
            .build());

        etfTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 6, 3))
            .userId(userId)
            .operation(TradeOperation.SELL)
            .price(BigDecimal.valueOf(50.33))
            .quantity(BigInteger.valueOf(24L))
            .intermediaryId(brokerId)
            .instrumentId(etfId)
            .build());
    }

    private void addBayer(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(47.23))
            .quantity(BigInteger.valueOf(110L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("BAYN", ExchangeEnum.XETRA.name()).getId())
            .build());
    }

    private void addAltria(Long userId, Map<String, Long> brokerNameById) {
        Long stockId = instrumentService.findInstrument("MO", USExchange.NYSE.name()).getId();
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(45.67))
            .quantity(BigInteger.valueOf(64L))
            .intermediaryId(brokerNameById.get("VTB"))
            .instrumentId(stockId)
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 6, 10))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(48.04))
            .quantity(BigInteger.valueOf(23L))
            .intermediaryId(brokerNameById.get("Interactive"))
            .instrumentId(stockId)
            .build());
    }

    private void addOmega(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(31.82))
            .quantity(BigInteger.valueOf(75L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("OHI", USExchange.NYSE.name()).getId())
            .build());
    }

    private void addCoke(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(52.04))
            .quantity(BigInteger.valueOf(24L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("KO", USExchange.NYSE.name()).getId())
            .build());
    }

    private void addTotal(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(45.3659))
            .quantity(BigInteger.valueOf(64L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("TTE", USExchange.NYSE.name()).getId())
            .build());
    }

    private void addRosneft(Long userId, Map<String, Long> brokerNameById) {
        Long stockId = instrumentService.findInstrument("ROSN", ExchangeEnum.MCX.name()).getId();
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 1, 18))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(393.15))
            .quantity(BigInteger.valueOf(700L))
            .instrumentId(stockId)
            .intermediaryId(brokerNameById.get("Tinkoff"))
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 3, 24))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(357.12))
            .quantity(BigInteger.valueOf(250L))
            .intermediaryId(brokerNameById.get("BCS"))
            .instrumentId(stockId)
            .build());
    }

    private void addGazprom(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(288.96))
            .quantity(BigInteger.valueOf(900L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("GAZP", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addRosAgro(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(1029.8))
            .quantity(BigInteger.valueOf(440L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("AGRO", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addPolyus(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(13179.4))
            .quantity(BigInteger.valueOf(27L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("PLZL", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addPhor(Long userId, Long brokerId) {
        var stockId = instrumentService.findInstrument("PHOR", ExchangeEnum.MCX.name()).getId();
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(4945))
            .quantity(BigInteger.valueOf(10L))
            .intermediaryId(brokerId)
            .instrumentId(stockId)
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 3, 24))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(4945))
            .quantity(BigInteger.valueOf(5L))
            .intermediaryId(brokerId)
            .instrumentId(stockId)
            .build());
    }

    private void addIRao(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(5.0253))
            .quantity(BigInteger.valueOf(10000L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("IRAO", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addMts(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(317.74))
            .quantity(BigInteger.valueOf(250L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("MTSS", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addMagnit(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(5222.2))
            .quantity(BigInteger.valueOf(16L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("MGNT", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addMoex(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(135.51))
            .quantity(BigInteger.valueOf(110L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("MOEX", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addFXCN(Long userId, Long brokerId) {
        etfTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(3726))
            .quantity(BigInteger.valueOf(44L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("FXCN", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addCNOOC(Long userId, Long brokerId) {
        Long stockId = instrumentService.findInstrument("0883", "HK").getId();
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 6, 2))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(12.04))
            .quantity(BigInteger.valueOf(1000L))
            .intermediaryId(brokerId)
            .instrumentId(stockId)
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 6, 16))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(10.86))
            .quantity(BigInteger.valueOf(1000L))
            .intermediaryId(brokerId)
            .instrumentId(stockId)
            .build());
    }

}
