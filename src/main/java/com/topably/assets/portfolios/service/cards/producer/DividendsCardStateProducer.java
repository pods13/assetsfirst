package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.DividendsCard;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendDetails;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendSummary;
import com.topably.assets.portfolios.domain.cards.output.dividend.DividendsCardData;
import com.topably.assets.portfolios.domain.cards.output.dividend.TimeFrameDividend;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.security.SecurityTrade;
import com.topably.assets.trades.service.SecurityTradeService;
import com.topably.assets.xrates.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.Principal;
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

@Service(CardContainerType.DIVIDENDS)
@RequiredArgsConstructor
public class DividendsCardStateProducer implements CardStateProducer<DividendsCard> {

    private final SecurityTradeService tradeService;
    private final DividendService dividendService;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, DividendsCard card) {
        var groupedTrades = tradeService.findUserDividendPayingTrades(user.getName()).stream()
                .collect(groupingBy(trade -> {
                    Security security = trade.getSecurity();
                    return new TickerSymbol(security.getTicker(), security.getExchange().getCode());
                }));
        var details = groupedTrades.entrySet().stream()
                .map(this::composeDividendDetails)
                .flatMap(Collection::stream)
                .filter(divDetails -> divDetails.getTotal().compareTo(BigDecimal.ZERO) > 0)
                .toList();
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
        return DividendsCardData.builder()
                .dividends(dividends)
                .build();
    }

    private Collection<DividendDetails> composeDividendDetails(Map.Entry<TickerSymbol, List<SecurityTrade>> tradesByKey) {
        var key = tradesByKey.getKey();
        Collection<Dividend> dividends = dividendService.findDividends(key.getSymbol(), key.getExchange());
        var quantity = BigInteger.ZERO;
        var dividendDetails = new ArrayList<DividendDetails>();
        var trades = tradesByKey.getValue();
        var currency = trades.iterator().hasNext() ? trades.iterator().next().getSecurity().getExchange().getCurrency() : null;
        int index = 0;
        for (Dividend dividend : dividends) {
            for (; index < trades.size(); index++) {
                SecurityTrade trade = trades.get(index);
                if (trade.getDate().toLocalDate().compareTo(dividend.getRecordDate()) < 0) {
                    //TODO sell case
                    quantity = quantity.add(trade.getQuantity());
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

    private Collection<DividendSummary> composeDividendSummary(Map<Integer, List<DividendDetails>> dividendsByYear) {
        return dividendsByYear.entrySet().stream()
                .map(divsByYear -> {
                    var totalValue = divsByYear.getValue().stream()
                            .map(div -> currencyService.convert(div.getTotal(), div.getCurrency()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);
                    return new DividendSummary(String.valueOf(divsByYear.getKey()), totalValue, divsByYear.getValue());
                }).collect(toList());
    }

    private Map<Integer, List<DividendDetails>> enrichWithMissingYears(Collection<TreeMap<Integer, List<DividendDetails>>> dividendsByYearForAllQuarters,
                                                                       Map<Integer, List<DividendDetails>> dividendsByYear) {
        var res = new TreeMap<>(dividendsByYear);
        dividendsByYearForAllQuarters.stream()
                .map(TreeMap::keySet)
                .flatMap(Collection::stream)
                .forEach(year -> res.putIfAbsent(year, Collections.emptyList()));
        return res;
    }
}
