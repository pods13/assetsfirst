package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.DividendGoalsCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.DividendGoalsCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.PositionItem;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.AggregatedTrade;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.Year;
import java.util.Arrays;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_GOALS)
@RequiredArgsConstructor
public class DividendGoalsCardStateProducer implements CardStateProducer<DividendGoalsCard> {

    private final TradeService tradeService;
    private final DividendService dividendService;
    private final ExchangeService exchangeService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendGoalsCard card) {
        var trades = tradeService.findUserAggregatedTrades(user.getName());
        var items = trades.stream()
                .map(t -> this.convertToPositionItems(t, card))
                .filter(p -> p.getCurrentYield().compareTo(BigDecimal.ZERO) > 0)
                .collect(toList());
        return DividendGoalsCardData.builder()
                .items(items)
                .build();
    }

    private PositionItem convertToPositionItems(AggregatedTrade trade, DividendGoalsCard card) {
        var averagePrice = trade.getTotal().divide(new BigDecimal(trade.getQuantity()), RoundingMode.HALF_EVEN);
        TickerSymbol tickerSymbol = trade.getIdentifier();
        var annualDividend = dividendService.calculateAnnualDividend(tickerSymbol, Year.now());
        var currentYield = annualDividend.multiply(BigDecimal.valueOf(100)).divide(averagePrice, 2, RoundingMode.HALF_EVEN);

        var desiredYield = card.getDesiredYieldByIssuer().getOrDefault(tickerSymbol.toString(), currentYield);
        var targets = new TreeSet<>(Arrays.asList(averagePrice, calculateDesiredPrice(annualDividend, desiredYield)));
        return PositionItem.builder()
                .name(tickerSymbol.toString())
                .averagePrice(averagePrice)
                .annualDividend(annualDividend)
                .currentYield(currentYield)
                .targets(targets)
                .build();
    }

    private BigDecimal calculateDesiredPrice(BigDecimal annualDividend, BigDecimal desiredYield) {
        if (desiredYield.compareTo(BigDecimal.ZERO) > 0) {
            return annualDividend.multiply(BigDecimal.valueOf(100)).divide(desiredYield, 2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }
}
