package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.core.util.NumberUtils;
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
import java.time.LocalDate;
import java.time.Year;

@Service(CardContainerType.INVESTMENT_YIELD)
@RequiredArgsConstructor
public class InvestmentYieldCardStateProducer implements CardStateProducer<InvestmentYieldCard> {

    private final PortfolioService portfolioService;

    @Override
    public CardData produce(Portfolio portfolio, InvestmentYieldCard card) {
        var annualDividend = portfolioService.calculateAnnualDividend(portfolio, Year.now());
        return new InvestmentYieldCardData()
            .setYieldOnCost(calculateYieldOnCost(portfolio, annualDividend))
            .setDividendYield(calculateDividendYield(portfolio, annualDividend));
    }

    private BigDecimal calculateYieldOnCost(Portfolio portfolio, BigDecimal annualDividend) {
        var investedAmount = portfolioService.calculateInvestedAmountInYieldInstrument(portfolio);
        return NumberUtils.calculatePercentage(investedAmount, annualDividend);
    }

    private BigDecimal calculateDividendYield(Portfolio portfolio, BigDecimal annualDividend) {
        var currentAmount = portfolioService.calculateMarketValueInYieldInstruments(portfolio, LocalDate.now());
        return NumberUtils.calculatePercentage(currentAmount, annualDividend);
    }
}
