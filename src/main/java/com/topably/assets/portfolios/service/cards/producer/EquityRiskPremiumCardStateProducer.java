package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.core.util.NumberUtils;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.EquityRiskPremiumCard;
import com.topably.assets.portfolios.domain.cards.output.risk.EquityRiskPremiumCardData;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.util.Currency;
import java.util.Set;

@Service(CardContainerType.EQUITY_RISK_PREMIUM)
@RequiredArgsConstructor
public class EquityRiskPremiumCardStateProducer implements CardStateProducer<EquityRiskPremiumCard> {

    private final PortfolioPositionService positionService;
    private final ExchangeService exchangeService;

    @Override
    public CardData produce(Portfolio portfolio, EquityRiskPremiumCard card) {
        //TODO take portfolio.currency from portfolio instance
        var portfolioCurrency = Currency.getInstance("RUB");
        //TODO make dynamic param
        var interestRate = BigDecimal.valueOf(12);
        var equityData = positionService.findPortfolioPositionsByPortfolioId(portfolio.getId()).stream()
            .filter(p -> p.getQuantity().compareTo(BigInteger.ZERO) > 0 && p.getInstrument().getExchangeCode().equals(ExchangeEnum.MCX.name()))
            .map(p -> {
                var ticker = p.getInstrument().toTicker();
                var price = exchangeService.findSymbolRecentPrice(ticker).orElse(p.getAveragePrice());
                var annualDividend = positionService.calculateAnnualDividend(p, Year.now());
                return new EquityRiskPremiumCardData.EquityData(ticker.toString(), NumberUtils.calculatePercentage(price, annualDividend).subtract(interestRate));
            })
            .toList();
        return new EquityRiskPremiumCardData()
            .setEquities(equityData);
    }
}
