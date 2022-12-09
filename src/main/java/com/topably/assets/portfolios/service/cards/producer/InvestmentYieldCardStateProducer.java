package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.InvestmentYieldCard;
import com.topably.assets.portfolios.domain.cards.output.InvestmentYieldCardData;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

@Service(CardContainerType.INVESTMENT_YIELD)
@RequiredArgsConstructor
public class InvestmentYieldCardStateProducer implements CardStateProducer<InvestmentYieldCard> {

    private final PortfolioService portfolioService;

    @Override
    public CardData produce(Portfolio portfolio, InvestmentYieldCard card) {
        var investedAmount = portfolioService.calculateInvestedAmount(portfolio);
        var annualDividend = portfolioService.calculateAnnualDividend(portfolio, Year.now());
        var ttmYieldOnCost = calculateTTMYieldOnCost(investedAmount, annualDividend);
        return new InvestmentYieldCardData()
            .setDividendYield(ttmYieldOnCost);
    }

    private BigDecimal calculateTTMYieldOnCost(BigDecimal investedAmount, BigDecimal annualDividend) {
        if (investedAmount.compareTo(BigDecimal.ZERO) > 0) {
            return annualDividend.multiply(BigDecimal.valueOf(100)).divide(investedAmount, 2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }
}
