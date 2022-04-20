package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.DividendGoalsCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.DividendGoalsCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.PositionItem;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.security.SecurityAggregatedTrade;
import com.topably.assets.trades.service.SecurityTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_GOALS)
@RequiredArgsConstructor
public class DividendGoalsCardStateProducer implements CardStateProducer<DividendGoalsCard> {

    private final SecurityTradeService tradeService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendGoalsCard card) {
        var trades = tradeService.findUserAggregatedTrades(user.getName());
        var items = trades.stream()
                .map(this::convertToPositionItems)
                .collect(toList());
        var extraExpenses = items.stream()
                .map(item -> {
                    BigInteger extraQuantity = card.getDesiredPositionByIssuer()
                            .getOrDefault(item.getName(), item.getQuantity())
                            .subtract(item.getQuantity());
                    return item.getAveragePrice().multiply(new BigDecimal(extraQuantity));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
        return DividendGoalsCardData.builder()
                .items(items)
                .extraExpenses(extraExpenses)
                .build();
    }

    private PositionItem convertToPositionItems(SecurityAggregatedTrade trade) {
        var averagePrice = trade.getTotal().divide(new BigDecimal(trade.getQuantity()), RoundingMode.HALF_EVEN);
        return PositionItem.builder()
                .name(trade.getIdentifier().toString())
                .quantity(trade.getQuantity())
                .averagePrice(averagePrice)
                .build();
    }
}
