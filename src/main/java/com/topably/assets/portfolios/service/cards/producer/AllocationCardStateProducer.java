package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.security.SecurityTrade;
import com.topably.assets.trades.service.SecurityTradeService;
import com.topably.assets.xrates.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Currency;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final SecurityTradeService tradeService;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, AllocationCard card) {
        var groupedTrades = tradeService.findUserTrades(user.getName()).stream()
                .collect(groupingBy(trade -> {
                    Security security = trade.getSecurity();
                    return new TickerSymbol(security.getExchange().getCode(), security.getTicker());
                }));
        List<AllocationSegment> segments = groupedTrades.entrySet().stream()
                .map(entry -> composeSegments(entry.getKey(), entry.getValue()))
                .filter(segment -> segment.getValue().compareTo(BigDecimal.ZERO) != 0)
                .collect(toList());
        return AllocationCardData.builder()
                .segments(segments)
                .totalInvested(calculateTotalInvested(segments))
                .build();
    }

    private AllocationSegment composeSegments(TickerSymbol key, List<SecurityTrade> trades) {
        BigDecimal total = trades.stream()
                .map(this::calculateTradeTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AllocationSegment(key.getSymbol() + "." + key.getExchange(), total);
    }

    private BigDecimal calculateTradeTotal(SecurityTrade trade) {
        var tradeCurrency = trade.getSecurity().getExchange().getCurrency();
        var price = exchangeRateService.convertCurrency(trade.getPrice(), tradeCurrency, DESTINATION_CURRENCY);
        BigDecimal total = price.multiply(new BigDecimal(trade.getQuantity()));
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return total.negate();
        }
        return total;
    }

    private BigDecimal calculateTotalInvested(List<AllocationSegment> segments) {
        return segments.stream()
                .map(AllocationSegment::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
