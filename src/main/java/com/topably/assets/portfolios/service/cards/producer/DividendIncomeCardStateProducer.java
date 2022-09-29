package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.DividendIncomeCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendDetails;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendIncomeCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendSummary;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameDividend;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameOption;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.DIVIDEND_INCOME)
@RequiredArgsConstructor
public class DividendIncomeCardStateProducer implements CardStateProducer<DividendIncomeCard> {

    private final TradeService tradeService;
    private final DividendService dividendService;
    private final CurrencyConverterService currencyConverterService;

    @Override
    public CardData produce(Portfolio portfolio, DividendIncomeCard card) {
        var groupedTrades = tradeService.findDividendPayingTrades(portfolio.getId()).stream()
                .collect(groupingBy(trade -> {
                    Instrument instrument = trade.getPortfolioHolding().getInstrument();
                    return new TickerSymbol(instrument.getTicker(), instrument.getExchange().getCode());
                }));
        var details = groupedTrades.entrySet().stream()
                .map(this::composeDividendDetails)
                .flatMap(Collection::stream)
                .filter(divDetails -> divDetails.getTotal().compareTo(BigDecimal.ZERO) > 0)
                .toList();
        return produceDividendsGroupedByTimeFrame(details, card.getTimeFrame());
    }

    private Collection<DividendDetails> composeDividendDetails(Map.Entry<TickerSymbol, List<Trade>> tradesByKey) {
        var key = tradesByKey.getKey();
        Collection<Dividend> dividends = dividendService.findDividends(key.getSymbol(), key.getExchange());
        var quantity = BigInteger.ZERO;
        var dividendDetails = new ArrayList<DividendDetails>();
        var trades = tradesByKey.getValue();
        var currency = trades.iterator().hasNext() ? trades.iterator().next().getPortfolioHolding().getInstrument().getExchange().getCurrency() : null;
        int index = 0;
        for (Dividend dividend : dividends) {
            for (; index < trades.size(); index++) {
                Trade trade = trades.get(index);
                if (trade.getDate().compareTo(dividend.getRecordDate()) < 0) {
                    var operationQty = TradeOperation.SELL.equals(trade.getOperation()) ? trade.getQuantity().negate() : trade.getQuantity();
                    quantity = quantity.add(operationQty);
                } else {
                    break;
                }
            }
            BigDecimal total = dividend.getAmount().multiply(new BigDecimal(quantity));
            var forecasted = dividend.getPayDate() == null;
            var payDate = Optional.ofNullable((dividend.getPayDate()))
                    .orElseGet(() -> dividend.getRecordDate().plus(1, ChronoUnit.MONTHS));
            dividendDetails.add(new DividendDetails(key.getSymbol(), payDate, forecasted, total, currency));
        }
        return dividendDetails;
    }

    private CardData produceDividendsGroupedByTimeFrame(List<DividendDetails> details, TimeFrameOption timeFrame) {
        switch (timeFrame) {
            case MONTH -> {
                return produceDividendsGroupedByMonth(details, timeFrame);
            }
            case QUARTER -> {
                return produceDividendsGroupedByQuarter(details, timeFrame);
            }
            default -> {
                var dividendsByYear = details.stream()
                        .collect(groupingBy(d -> d.getPayDate().getYear()));
                var dividends = new TimeFrameDividend("Annual", composeDividendSummary(dividendsByYear));

                return DividendIncomeCardData.builder()
                        .dividends(List.of(dividends))
                        .build();
            }
        }
    }

    private CardData produceDividendsGroupedByMonth(List<DividendDetails> details, TimeFrameOption timeFrame) {
        var dividendsByYearMonth = details.stream()
                .collect(groupingBy(d -> d.getPayDate().getMonthValue(), TreeMap::new,
                        groupingBy(d -> d.getPayDate().getYear(), TreeMap::new, toList())));
        var dividends = dividendsByYearMonth.entrySet().stream()
                .map(divsByMonth -> {
                    var divsByYear = enrichWithMissingYears(dividendsByYearMonth.values(), divsByMonth.getValue());
                    return new TimeFrameDividend("M" + divsByMonth.getKey(),
                            composeDividendSummary(divsByYear));
                })
                .toList();
        return DividendIncomeCardData.builder()
                .dividends(dividends)
                .build();
    }

    private CardData produceDividendsGroupedByQuarter(List<DividendDetails> details, TimeFrameOption timeFrame) {
        var dividendsByYearQuarter = details.stream()
                .collect(groupingBy(d -> d.getPayDate().get(IsoFields.QUARTER_OF_YEAR), TreeMap::new,
                        groupingBy(d -> d.getPayDate().getYear(), TreeMap::new, toList())));
        var dividends = dividendsByYearQuarter.entrySet().stream()
                .map(divsByQuarter -> {
                    var divsByYear = enrichWithMissingYears(dividendsByYearQuarter.values(), divsByQuarter.getValue());
                    return new TimeFrameDividend("Q" + divsByQuarter.getKey(),
                            composeDividendSummary(divsByYear));
                })
                .toList();
        return DividendIncomeCardData.builder()
                .dividends(dividends)
                .build();
    }

    private Collection<DividendSummary> composeDividendSummary(Map<Integer, List<DividendDetails>> groupedDividends) {
        return groupedDividends.entrySet().stream()
                .map(divsByTimeFrame -> {
                    var totalValue = divsByTimeFrame.getValue().stream()
                            .map(div -> currencyConverterService.convert(div.getTotal(), div.getCurrency()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);
                    return new DividendSummary(String.valueOf(divsByTimeFrame.getKey()), totalValue, divsByTimeFrame.getValue());
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
