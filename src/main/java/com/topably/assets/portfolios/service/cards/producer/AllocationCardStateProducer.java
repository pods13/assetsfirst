package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.TradeGroupingKey;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.TradeViewId;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Currency;
import java.util.List;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final TradeService tradeService;
    private final ExchangeRateService exchangeRateService;

    @Override
    public PortfolioCardData produce(Principal user, AllocationCard card) {
        var groupedTrades = tradeService.getUserTrades(user.getName()).stream()
                .collect(groupingBy(trade -> new TradeGroupingKey(trade.getTicker(), trade.getTradeCategory(), trade.getUsername())));
        List<AllocationSegment> segments = groupedTrades.values().stream()
                .map(this::composeSegments)
                .filter(segment -> segment.getValue().compareTo(BigDecimal.ZERO) != 0)
                .collect(toList());
        return AllocationCardData.builder()
                .segments(segments)
                .build();
    }

    private AllocationSegment composeSegments(List<TradeView> trades) {
        BigDecimal total = trades.stream()
                .map(this::calculateTradeTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AllocationSegment(trades.get(0).getName(), total);
    }

    private BigDecimal calculateTradeTotal(TradeView trade) {
        BigDecimal total = convertToDestCurrency(trade).multiply(new BigDecimal(trade.getQuantity()));
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return total.negate();
        }
        return total;
    }

    private BigDecimal convertToDestCurrency(TradeView trade) {
        if (DESTINATION_CURRENCY.equals(trade.getCurrency())) {
            return trade.getPrice();
        }
        return exchangeRateService.convertCurrency(trade.getPrice(), trade.getCurrency(), DESTINATION_CURRENCY);
    }
}
