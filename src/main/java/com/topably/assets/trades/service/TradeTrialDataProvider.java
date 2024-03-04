package com.topably.assets.trades.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.stream.Collectors;

import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.broker.BrokerDto;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import com.topably.assets.trades.service.broker.BrokerService;
import com.topably.assets.trades.service.instrument.StockTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TradeTrialDataProvider {

    private final BrokerService brokerService;
    private final InstrumentService instrumentService;
    private final StockTradeService stockTradeService;

    public void provideData(Long userId) {
        var brokerNameById = brokerService.getBrokers(userId).stream()
            .collect(Collectors.toMap(b -> b.name().split(" ")[0], BrokerDto::id));

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
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("GAZP", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(1029.8))
            .quantity(BigInteger.valueOf(500L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("AGRO", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(13179.4))
            .quantity(BigInteger.valueOf(25L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("PLZL", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2021, 12, 1))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(4945))
            .quantity(BigInteger.valueOf(25L))
            .intermediaryId(brokerId)
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
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("ROSN", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 12, 19))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(157.9))
            .quantity(BigInteger.valueOf(1000L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("GAZP", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 12, 19))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(7491))
            .quantity(BigInteger.valueOf(25L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("PLZL", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 9, 20))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(120))
            .quantity(BigInteger.valueOf(1000L))
            .intermediaryId(brokerId)
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
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("ROSN", ExchangeEnum.MCX.name()).getId())
            .build());

        stockTradeService.addTrade(AddTradeDto.builder()
            .date(LocalDate.of(2022, 10, 4))
            .userId(userId)
            .operation(TradeOperation.BUY)
            .price(BigDecimal.valueOf(6038))
            .quantity(BigInteger.valueOf(25L))
            .intermediaryId(brokerId)
            .instrumentId(instrumentService.findInstrument("PHOR", ExchangeEnum.MCX.name()).getId())
            .build());
    }

}
