package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.findata.dividends.domain.dto.AggregatedDividendDto;
import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.ContributionCard;
import com.topably.assets.portfolios.domain.cards.output.ContributionCardData;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
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
        var currentYearTrades = tradeService.getUserTradesForCurrentYear(portfolio).stream()
            .filter(t -> !InstrumentType.FX.name().equals(t.getInstrumentType()))
            .toList();
        var tradesByMonthValue = currentYearTrades.stream()
            .collect(Collectors.groupingBy(t -> t.getDate().getMonthValue(), TreeMap::new, Collectors.toList()));
        var dividendYears = Set.of(LocalDate.now().getYear());
        var trades = tradeService.findDividendPayingTrades(portfolio.getId(), dividendYears);
        var dividendsByMonthValue = dividendService.aggregateDividends(trades, dividendYears).stream()
            .collect(Collectors.groupingBy(d -> d.getPayDate().getMonthValue(), TreeMap::new, Collectors.toList()));

        var xAxis = composeXAxis();
        var contributions = composeContributions(tradesByMonthValue, dividendsByMonthValue);
        return new ContributionCardData()
            .setXaxis(xAxis)
            .setContributions(contributions)
            .setTotalContributed(calculateTotalContributed(contributions))
            .setCurrencyCode(portfolio.getCurrency().getCurrencyCode());
    }

    private Collection<String> composeXAxis() {
        return EnumSet.allOf(Month.class).stream()
            .map(m -> Month.of(m.getValue()).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH))
            .toList();
    }

    private Collection<ContributionCardData.Contribution> composeContributions(TreeMap<Integer, List<TradeView>> tradesByMonthValue,
                                                                               TreeMap<Integer, List<AggregatedDividendDto>> dividendsByMonthValue) {
        var dividendContributions = new ArrayList<BigDecimal>();
        var depositContributions = new ArrayList<BigDecimal>();
        EnumSet.allOf(Month.class).forEach(month -> {
            var monthValue = month.getValue();
            var monthlyDividend = calculateTotalMonthlyDividend(dividendsByMonthValue, monthValue);
            dividendContributions.add(monthlyDividend.setScale(2, RoundingMode.HALF_UP));

            var monthTrades = tradesByMonthValue.getOrDefault(monthValue, Collections.emptyList());
            depositContributions.add(calculateMonthlyContribution(monthTrades, monthlyDividend));
        });

        return List.of(new ContributionCardData.Contribution(DIVIDEND_CONTRIBUTION_NAME, dividendContributions),
            new ContributionCardData.Contribution(DEPOSIT_CONTRIBUTION_NAME, depositContributions));
    }

    private BigDecimal calculateMonthlyContribution(List<TradeView> monthTrades, BigDecimal monthlyDividend) {
        var monthlyPurchases = monthTrades.stream()
            .map(t -> {
                var total = currencyConverterService.convert(t.getPrice().multiply(new BigDecimal(t.getQuantity())), t.getCurrency());
                return TradeOperation.SELL.equals(t.getOperation()) ? total.multiply(BigDecimal.valueOf(-1L)) : total;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (monthlyPurchases.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return monthlyPurchases.subtract(monthlyDividend)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalMonthlyDividend(TreeMap<Integer, List<AggregatedDividendDto>> dividendsByMonthValue, int monthValue) {
        return dividendsByMonthValue.getOrDefault(monthValue, Collections.emptyList()).stream()
            .map(d -> currencyConverterService.convert(d.getTotal(), d.getCurrency()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalContributed(Collection<ContributionCardData.Contribution> contributions) {
        return contributions.stream()
            .map(ContributionCardData.Contribution::data)
            .flatMap(Collection::stream)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
