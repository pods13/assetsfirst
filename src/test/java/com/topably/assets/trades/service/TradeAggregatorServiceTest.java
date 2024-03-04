package com.topably.assets.trades.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.repository.TradeViewRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


class TradeAggregatorServiceTest {

    private final TradeAggregatorService service = new TradeAggregatorService(mock(TradeViewRepository.class));

    @Test
    public void givenTwoBuyTrades_whenAggregationTradeCalculated_thenReturnAvgPrice() {
        var buyTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var buyTrade2 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.BUY);
        var expectedShares = new BigInteger("200");
        var expectedPrice = new BigDecimal("250");

        var res = service.aggregateTrades(List.of(buyTrade, buyTrade2));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(res.getPnl()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(res.getDeltaPnls()).hasSize(0);
    }

    @Test
    public void givenBuyTradeAndSellTrade_whenAggregationTradeCalculated_thenReturnAvgPrice() {
        var buyTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY)
            .setCurrency(Currency.getInstance("RUB"));
        var sellTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("50"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL)
            .setCurrency(Currency.getInstance("RUB"));
        var expectedShares = new BigInteger("50");
        var expectedPrice = new BigDecimal("300");

        var res = service.aggregateTrades(List.of(buyTrade, sellTrade));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(res.getDeltaPnls()).containsExactly(
            new AggregatedTradeDto.DeltaPnl(buyTrade.getDate(),
                sellTrade.getDate(),
                buyTrade.getPrice().multiply(new BigDecimal(sellTrade.getQuantity())),
                sellTrade.getPrice().multiply(new BigDecimal(sellTrade.getQuantity())),
                sellTrade.getQuantity(),
                sellTrade.getCurrency())
        );
    }

    @Test
    public void givenTradesHistory_whenAggregationTradeCalculated_thenFIFOMethodIsUsed() {
        var buyTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var buyTrade2 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now().minusDays(15))
            .setOperation(TradeOperation.BUY);
        var buyTrade3 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now().minusDays(5))
            .setOperation(TradeOperation.BUY);
        var sellTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("150"))
            .setPrice(new BigDecimal("200"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var sellTrade2 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
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
        var currency = Currency.getInstance("RUB");
        var buyTrade = new TradeView()
            .setIntermediaryId(2L)
            .setIntermediaryName("Finam")
            .setQuantity(new BigInteger("440"))
            .setPrice(new BigDecimal("1029.8"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY)
            .setCurrency(currency);
        var buyTrade2 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("750"))
            .setDate(LocalDate.now().minusDays(15))
            .setCurrency(currency)
            .setOperation(TradeOperation.BUY);
        var buyTrade3 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("699"))
            .setDate(LocalDate.now().minusDays(5))
            .setCurrency(currency)
            .setOperation(TradeOperation.BUY);
        var buyTrade4 = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("30"))
            .setPrice(new BigDecimal("692"))
            .setDate(LocalDate.now().minusDays(3))
            .setCurrency(currency)
            .setOperation(TradeOperation.BUY);
        var sellTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("90"))
            .setPrice(new BigDecimal("688"))
            .setDate(LocalDate.now())
            .setCurrency(currency)
            .setOperation(TradeOperation.SELL);
        var expectedShares = new BigInteger("440");
        var expectedPrice = new BigDecimal("1029.8");
        var expectedPnl = new BigDecimal("-2310");

        var res = service.aggregateTrades(List.of(buyTrade, buyTrade2, buyTrade3, buyTrade4, sellTrade));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(res.getPnl()).isEqualByComparingTo(expectedPnl);
        assertThat(res.getDeltaPnls()).containsExactlyInAnyOrder(
            new AggregatedTradeDto.DeltaPnl(buyTrade2.getDate(),
                sellTrade.getDate(),
                buyTrade2.getTotal(),
                sellTrade.getPrice().multiply(new BigDecimal(buyTrade2.getQuantity())),
                buyTrade2.getQuantity(),
                sellTrade.getCurrency()),
            new AggregatedTradeDto.DeltaPnl(buyTrade3.getDate(),
                sellTrade.getDate(),
                buyTrade3.getTotal(),
                sellTrade.getPrice().multiply(new BigDecimal(buyTrade3.getQuantity())),
                buyTrade3.getQuantity(),
                sellTrade.getCurrency()),
            new AggregatedTradeDto.DeltaPnl(buyTrade4.getDate(),
                sellTrade.getDate(),
                buyTrade4.getTotal(),
                sellTrade.getPrice().multiply(new BigDecimal(buyTrade4.getQuantity())),
                buyTrade4.getQuantity(),
                sellTrade.getCurrency())
        );
    }

    @Test
    public void givenBuyTradeAndSellTrade_whenAggregationTradeCalculated_thenClosedPnlIsCalculated() {
        var buyTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("300"))
            .setDate(LocalDate.now().minusDays(30))
            .setOperation(TradeOperation.BUY);
        var sellTrade = new TradeView()
            .setIntermediaryId(1L)
            .setIntermediaryName("Tinkoff")
            .setQuantity(new BigInteger("100"))
            .setPrice(new BigDecimal("400"))
            .setDate(LocalDate.now())
            .setOperation(TradeOperation.SELL);
        var expectedShares = new BigInteger("0");
        var expectedPrice = new BigDecimal("0");
        var expectedRealizedPnl = new BigDecimal("10000");

        var res = service.aggregateTrades(List.of(buyTrade, sellTrade));

        assertThat(res.getQuantity()).isEqualByComparingTo(expectedShares);
        assertThat(res.getPrice()).isEqualByComparingTo(expectedPrice);
        assertThat(res.getPnl()).isEqualByComparingTo(expectedRealizedPnl);
        assertThat(res.getDeltaPnls()).containsExactlyInAnyOrder(
            new AggregatedTradeDto.DeltaPnl(buyTrade.getDate(),
                sellTrade.getDate(),
                buyTrade.getTotal(),
                sellTrade.getTotal(),
                sellTrade.getQuantity(),
                sellTrade.getCurrency())
        );
    }

}
