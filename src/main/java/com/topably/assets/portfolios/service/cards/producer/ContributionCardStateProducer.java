package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.ContributionCard;
import com.topably.assets.portfolios.domain.cards.output.ContributionCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service(CardContainerType.CONTRIBUTION)
@RequiredArgsConstructor
public class ContributionCardStateProducer implements CardStateProducer<ContributionCard> {

    private final TradeService tradeService;
    private final CurrencyConverterService currencyConverterService;

    @Override
    public CardData produce(Portfolio portfolio, ContributionCard card) {
        var currentYearTrades = tradeService.getUserTradesForCurrentYear(portfolio);
        var tradesByMonthValue = currentYearTrades.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().getMonthValue(), TreeMap::new, Collectors.toList()));
        var contributions = tradesByMonthValue.keySet().stream()
                .map(monthValue -> {
                    //TODO include reinvested divs to contributions
                    var depositContribution = new ContributionCardData.ContributionDetails("Deposit", tradesByMonthValue.get(monthValue).stream()
                            .filter(t -> TradeOperation.BUY.equals(t.getOperation()))
                            .map(t -> currencyConverterService.convert(t.getPrice().multiply(new BigDecimal(t.getQuantity())), t.getCurrency()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    var monthDisplayName = Month.of(monthValue).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH);
                    return new ContributionCardData.Contribution(monthDisplayName, List.of(depositContribution));
                })
                .toList();

        return new ContributionCardData()
                .setContributions(contributions);
    }
}
