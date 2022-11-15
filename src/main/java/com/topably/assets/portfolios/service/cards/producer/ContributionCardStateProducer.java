package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.dividends.domain.dto.AggregatedDividendDto;
import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.ContributionCard;
import com.topably.assets.portfolios.domain.cards.output.ContributionCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service(CardContainerType.CONTRIBUTION)
@RequiredArgsConstructor
public class ContributionCardStateProducer implements CardStateProducer<ContributionCard> {

    private static final String DIVIDEND_CONTRIBUTION_NAME = "Dividend";
    private static final String DEPOSIT_CONTRIBUTION_NAME = "Deposit";
    private final TradeService tradeService;
    private final CurrencyConverterService currencyConverterService;
    private final DividendService dividendService;

    @Override
    public CardData produce(Portfolio portfolio, ContributionCard card) {
        var currentYearTrades = tradeService.getUserTradesForCurrentYear(portfolio);
        var tradesByMonthValue = currentYearTrades.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().getMonthValue(), TreeMap::new, Collectors.toList()));
        var dividendYears = Set.of(LocalDate.now().getYear());
        var trades = tradeService.findDividendPayingTrades(portfolio.getId(), dividendYears);
        var dividendsByMonthValue = dividendService.aggregateDividends(trades, dividendYears).stream()
                .collect(Collectors.groupingBy(d -> d.getPayDate().getMonthValue(), TreeMap::new, Collectors.toList()));
        var contributions = EnumSet.allOf(Month.class).stream()
                .map(month -> {
                    var monthValue = month.getValue();
                    var monthlyDividend = calculateTotalMontlyDividend(dividendsByMonthValue, monthValue);
                    var dividendContribution = new ContributionCardData.ContributionDetails(DIVIDEND_CONTRIBUTION_NAME,
                            monthlyDividend.setScale(2, RoundingMode.HALF_UP));

                    var monthTrades = tradesByMonthValue.getOrDefault(monthValue, Collections.emptyList());
                    var depositContribution = new ContributionCardData.ContributionDetails(DEPOSIT_CONTRIBUTION_NAME,
                            calculateMonthlyContribution(monthTrades, monthlyDividend));
                    var monthDisplayName = Month.of(monthValue).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH);
                    return new ContributionCardData.Contribution(monthDisplayName, List.of(dividendContribution, depositContribution));
                })
                .toList();

        return new ContributionCardData()
                .setContributions(contributions);
    }

    private BigDecimal calculateMonthlyContribution(List<TradeView> monthTrades, BigDecimal monthlyDividend) {
        return monthTrades.stream()
                .filter(t -> TradeOperation.BUY.equals(t.getOperation()))
                .map(t -> currencyConverterService.convert(t.getPrice().multiply(new BigDecimal(t.getQuantity())), t.getCurrency()))
                .reduce(BigDecimal.ZERO, BigDecimal::add).subtract(monthlyDividend)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalMontlyDividend(TreeMap<Integer, List<AggregatedDividendDto>> dividendsByMonthValue, int monthValue) {
        return dividendsByMonthValue.getOrDefault(monthValue, Collections.emptyList()).stream()
                .map(d -> currencyConverterService.convert(d.getTotal(), d.getCurrency()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
