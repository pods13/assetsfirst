package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.dividend.AnnualDividendProjection;
import com.topably.assets.portfolios.domain.cards.input.dividend.DividendIncomeCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendDetails;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendIncomeCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendSummary;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameDividend;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_INCOME)
@RequiredArgsConstructor
public class DividendIncomeCardStateProducer implements CardStateProducer<DividendIncomeCard> {

    private final TradeService tradeService;
    private final DividendService dividendService;
    private final CurrencyConverterService currencyConverterService;
    private final PortfolioPositionService portfolioPositionService;

    @Override
    public CardData produce(Portfolio portfolio, DividendIncomeCard card) {
        var currentYear = LocalDate.now().getYear();
        var dividendYears = List.of(currentYear - 1, currentYear, currentYear + 1);
        var trades = tradeService.findDividendPayingTrades(portfolio.getId(), dividendYears);

        var details = dividendService.aggregateDividends(trades, dividendYears).stream()
            .map(d -> new DividendDetails(d.getTicker().getSymbol(), d.getPayDate(), d.isForecasted(), d.getTotal(), d.getCurrency()))
            .toList();
        return produceDividendsGroupedByTimeFrame(portfolio, details, card);
    }

    private CardData produceDividendsGroupedByTimeFrame(Portfolio portfolio, List<DividendDetails> details, DividendIncomeCard card) {
        switch (card.getTimeFrame()) {
            case MONTH -> {
                return produceDividendsGroupedByMonth(portfolio, details);
            }
            case QUARTER -> {
                return produceDividendsGroupedByQuarter(portfolio, details);
            }
            default -> {
                var dividendsByYear = details.stream()
                    .collect(groupingBy(d -> d.getPayDate().getYear()));

                var dividends = new TimeFrameDividend("Annual", composeAnnualDividendSummary(portfolio, card, dividendsByYear));
                return new DividendIncomeCardData()
                    .setDividends(List.of(dividends));
            }
        }
    }

    private Collection<DividendSummary> composeAnnualDividendSummary(Portfolio portfolio, DividendIncomeCard card,
                                                                     Map<Integer, List<DividendDetails>> dividendsByYear) {
        var dividendSummaries = composeDividendSummary(portfolio, dividendsByYear);
        if (Boolean.TRUE.equals(card.getUseCustomDividendProjections())) {
            var tickerByAnnualDividendProjection = card.getAnnualDividendProjections().stream()
                .collect(Collectors.toMap(AnnualDividendProjection::ticker, Function.identity()));
            var totalForecasted = portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
                .filter(p -> tickerByAnnualDividendProjection.containsKey(p.getIdentifier().toString()))
                .map(p -> {
                    var projection = tickerByAnnualDividendProjection.get(p.getIdentifier().toString());
                    var projectedDividendAmount = new BigDecimal(p.getQuantity()).multiply(projection.dividend());
                    return currencyConverterService.convert(projectedDividendAmount, projection.currency(), p.getCurrency());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
            var name = String.valueOf(Collections.max(dividendsByYear.keySet()));
            var forecastedSummary = new DividendSummary()
                .setName(name)
                .setStack(name)
                .setDetails(List.of())
                .setValue(totalForecasted)
                .setCurrencyCode(portfolio.getCurrency().getCurrencyCode());
            return Stream.concat(dividendSummaries.stream(), Stream.of(forecastedSummary))
                .toList();
        }
        return dividendSummaries;
    }

    private CardData produceDividendsGroupedByMonth(Portfolio portfolio, List<DividendDetails> details) {
        var dividendsByYearMonth = details.stream()
            .collect(groupingBy(d -> d.getPayDate().getMonthValue(), TreeMap::new,
                groupingBy(d -> d.getPayDate().getYear(), TreeMap::new, toList())));
        var dividends = dividendsByYearMonth.entrySet().stream()
            .map(divsByMonth -> {
                var divsByYear = enrichWithMissingYears(dividendsByYearMonth.values(), divsByMonth.getValue());
                var monthDisplayName = Month.of(divsByMonth.getKey()).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH);
                return new TimeFrameDividend(monthDisplayName, composeDividendSummary(portfolio, divsByYear));
            })
            .toList();
        return new DividendIncomeCardData()
            .setDividends(dividends);
    }

    private CardData produceDividendsGroupedByQuarter(Portfolio portfolio, List<DividendDetails> details) {
        var dividendsByYearQuarter = details.stream()
            .collect(groupingBy(d -> d.getPayDate().get(IsoFields.QUARTER_OF_YEAR), TreeMap::new,
                groupingBy(d -> d.getPayDate().getYear(), TreeMap::new, toList())));
        var dividends = dividendsByYearQuarter.entrySet().stream()
            .map(divsByQuarter -> {
                var divsByYear = enrichWithMissingYears(dividendsByYearQuarter.values(), divsByQuarter.getValue());
                return new TimeFrameDividend("Q" + divsByQuarter.getKey(),
                    composeDividendSummary(portfolio, divsByYear));
            })
            .toList();
        return new DividendIncomeCardData()
            .setDividends(dividends);
    }

    private Collection<DividendSummary> composeDividendSummary(Portfolio portfolio, Map<Integer, List<DividendDetails>> groupedDividends) {
        return groupedDividends.entrySet().stream()
            .map(divsByTimeFrame -> {
                var totalValue = divsByTimeFrame.getValue().stream()
                    .map(div -> {
                        var time = div.getPayDate().atStartOfDay().toInstant(ZoneOffset.UTC);
                        return currencyConverterService.convert(div.getTotal(), div.getCurrency(), portfolio.getCurrency(), time);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
                var name = String.valueOf(divsByTimeFrame.getKey());
                return new DividendSummary()
                    .setName(name)
                    .setStack(name)
                    .setValue(totalValue)
                    .setDetails(divsByTimeFrame.getValue())
                    .setCurrencyCode(portfolio.getCurrency().getCurrencyCode());
            }).collect(toList());
    }

    private Map<Integer, List<DividendDetails>> enrichWithMissingYears(Collection<TreeMap<Integer, List<DividendDetails>>> dividendsByYearForAllTimeFrames,
                                                                       Map<Integer, List<DividendDetails>> dividendsByYear) {
        var res = new TreeMap<>(dividendsByYear);
        dividendsByYearForAllTimeFrames.stream()
            .map(TreeMap::keySet)
            .flatMap(Collection::stream)
            .forEach(year -> res.putIfAbsent(year, Collections.emptyList()));
        return res;
    }
}
