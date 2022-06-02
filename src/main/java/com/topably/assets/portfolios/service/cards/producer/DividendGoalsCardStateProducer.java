package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.DividendGoalsCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.DividendGoalsCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.goal.PositionItem;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.Arrays;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_GOALS)
@RequiredArgsConstructor
public class DividendGoalsCardStateProducer implements CardStateProducer<DividendGoalsCard> {

    private final DividendService dividendService;
    private final PortfolioHoldingService portfolioHoldingService;

    @Override
    public CardData produce(Portfolio portfolio, DividendGoalsCard card) {
        var holdingDtos = portfolioHoldingService.findPortfolioHoldings(portfolio.getId());
        var items = holdingDtos.stream()
                .map(h -> this.convertToPositionItems(h, card))
                .filter(p -> p.getCurrentYield().compareTo(BigDecimal.ZERO) > 0)
                .collect(toList());
        return DividendGoalsCardData.builder()
                .items(items)
                .build();
    }

    private PositionItem convertToPositionItems(PortfolioHoldingDto holding, DividendGoalsCard card) {
        var averagePrice = holding.getTotal().divide(new BigDecimal(holding.getQuantity()), RoundingMode.HALF_EVEN);
        TickerSymbol tickerSymbol = holding.getIdentifier();
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
