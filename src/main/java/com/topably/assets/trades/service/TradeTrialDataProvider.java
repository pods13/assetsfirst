package com.topably.assets.trades.service;

import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import com.topably.assets.trades.service.instrument.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeTrialDataProvider {

    private final BrokerRepository brokerRepository;
    private final InstrumentService instrumentService;
    private final StockTradeService stockTradeService;

    public void provideData(Long userId) {
        var brokerNameById = brokerRepository.findAll().stream()
            .collect(Collectors.toMap(b -> b.getName().split(" ")[0], Broker::getId));

        addBCSTrades(userId, brokerNameById.get("BCS"));
        addVTBTrades(userId, brokerNameById.get("VTB"));
        addTinkoffTrades(userId, brokerNameById.get("Tinkoff"));
    }

    private void addBCSTrades(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(288.96))
            .quantity(BigInteger.valueOf(1000L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("GAZP", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(1029.8))
            .quantity(BigInteger.valueOf(500L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("AGRO", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(13179.4))
            .quantity(BigInteger.valueOf(25L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("PLZL", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(4945))
            .quantity(BigInteger.valueOf(25L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("PHOR", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addVTBTrades(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 9, 20))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(329))
            .quantity(BigInteger.valueOf(1000L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("ROSN", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 12, 19))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(157.9))
            .quantity(BigInteger.valueOf(1000L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("GAZP", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 12, 19))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(7491))
            .quantity(BigInteger.valueOf(25L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("PLZL", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 9, 20))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(120))
            .quantity(BigInteger.valueOf(1000L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("SBERP", ExchangeEnum.MCX.name()).getId())
            .build());
    }

    private void addTinkoffTrades(Long userId, Long brokerId) {
        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 1, 18))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(393.15))
            .quantity(BigInteger.valueOf(1000L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("ROSN", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 10, 4))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(6038))
            .quantity(BigInteger.valueOf(25L))
            .brokerId(brokerId)
            .instrumentId(instrumentService.findInstrument("PHOR", ExchangeEnum.MCX.name()).getId())
            .build());
    }
}
