package com.topably.assets.trades.service;

import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.broker.Broker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeAggregatorServiceTest {

    private final TradeAggregatorService service = new TradeAggregatorService();

    @Test
    public void givenTwoBuyTrades_whenAggregationTradeCalculated_thenReturnAvgPrice() {
        var buyTrade = new Trade()
            .setBroker(new Broker().setId(1L).setName("Tinkoff"))
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var buyTrade2 = new Trade()
            .setBroker(new Broker().setId(2L).setName("Tinkoff"))
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.BUY);
        var expectedShares = new BigInteger("200");
        var expectedPrice = new BigDecimal("250");

        var res = service.aggregateTrades(List.of(buyTrade, buyTrade2));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @Test
    public void givenBuyTradeAndSellTrade_whenAggregationTradeCalculated_thenReturnAvgPrice() {
        var buyTrade = new Trade()
            .setBroker(new Broker().setId(1L).setName("Tinkoff"))
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var sellTrade = new Trade()
            .setBroker(new Broker().setId(2L).setName("Tinkoff"))
            .setQuantity(new BigInteger("50"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var expectedShares = new BigInteger("50");
        var expectedPrice = new BigDecimal("300");

        var res = service.aggregateTrades(List.of(buyTrade, sellTrade));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @Test
    public void givenTradesHistory_whenAggregationTradeCalculated_thenFIFOMethodIsUsed() {
        var broker = new Broker().setId(1L).setName("Tinkoff");
        var buyTrade = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var buyTrade2 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now().minusDays(15))
            .setOperation(TradeOperation.BUY);
        var buyTrade3 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now().minusDays(5))
            .setOperation(TradeOperation.BUY);
        var sellTrade = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("150"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var sellTrade2 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var expectedShares = new BigInteger("50");
        var expectedPrice = new BigDecimal("200");

        var res = service.aggregateTrades(List.of(buyTrade, buyTrade2, buyTrade3, sellTrade, sellTrade2));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
    }

    @Test
    public void givenTradesHistoryOnDifferentBrokers_whenAggregationTradeCalculated_thenFIFOMethodIsUsedWithBrokerConsideration() {
        var broker = new Broker().setId(1L).setName("Tinkoff");
        var broker2 = new Broker().setId(2L).setName("Finam");
        var buyTrade = new Trade()
            .setBroker(broker2)
            .setQuantity(new BigInteger("440"))
            .setPrice(new BigDecimal("1029.8"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var buyTrade2 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("750"))
            .setDate(LocalDate.now().minusDays(15))
            .setOperation(TradeOperation.BUY);
        var buyTrade3 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("699"))
            .setDate(LocalDate.now().minusDays(5))
            .setOperation(TradeOperation.BUY);
        var buyTrade4 = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("692"))
            .setDate(LocalDate.now().minusDays(3))
            .setOperation(TradeOperation.BUY);
        var sellTrade = new Trade()
            .setBroker(broker)
            .setQuantity(new BigInteger("90"))
            .setPrice(new BigDecimal("688"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var expectedShares = new BigInteger("440");
        var expectedPrice = new BigDecimal("1029.8");
        var expectedPnl = new BigDecimal("-2310");

        var res = service.aggregateTrades(List.of(buyTrade, buyTrade2, buyTrade3, buyTrade4, sellTrade));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(res.getInterimTradeResult().closedPnl()).isEqualByComparingTo(expectedPnl);
    }

}
