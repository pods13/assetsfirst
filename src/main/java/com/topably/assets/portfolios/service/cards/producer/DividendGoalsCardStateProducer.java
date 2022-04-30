package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.dividends.service.DividendService;
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
import java.math.RoundingMode;
import java.security.Principal;
import java.time.Year;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_GOALS)
@RequiredArgsConstructor
public class DividendGoalsCardStateProducer implements CardStateProducer<DividendGoalsCard> {

    private final SecurityTradeService tradeService;
    private final DividendService dividendService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendGoalsCard card) {
        var trades = tradeService.findUserAggregatedTrades(user.getName());
        var items = trades.stream()
                .map(this::convertToPositionItems)
                .filter(p -> p.getCurrentYield().compareTo(BigDecimal.ZERO) > 0)
                .collect(toList());
        return DividendGoalsCardData.builder()
                .items(items)
                .build();
    }

    private PositionItem convertToPositionItems(SecurityAggregatedTrade trade) {
        var averagePrice = trade.getTotal().divide(new BigDecimal(trade.getQuantity()), RoundingMode.HALF_EVEN);
        var annualDividend = dividendService.calculateAnnualDividend(trade.getIdentifier(), Year.now().minusYears(1));
        var currentYield = annualDividend.multiply(BigDecimal.valueOf(100)).divide(averagePrice, 2, RoundingMode.HALF_EVEN);
        return PositionItem.builder()
                .name(trade.getIdentifier().toString())
                .averagePrice(averagePrice)
                .annualDividend(annualDividend)
                .currentYield(currentYield)
                .build();
    }
}
