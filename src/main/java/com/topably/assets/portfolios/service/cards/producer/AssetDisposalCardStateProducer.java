package com.topably.assets.portfolios.service.cards.producer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverter;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.AssetDisposalCard;
import com.topably.assets.portfolios.domain.cards.output.disposal.AssetDisposalCardData;
import com.topably.assets.portfolios.domain.cards.output.disposal.AssetDisposalDetails;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service(CardContainerType.ASSET_DISPOSAL)
@RequiredArgsConstructor
@Slf4j
public class AssetDisposalCardStateProducer implements CardStateProducer<AssetDisposalCard> {

    private final PortfolioPositionService portfolioPositionService;
    private final TradeAggregatorService tradeAggregatorService;
    private final CurrencyConverter currencyConverter;

    @Override
    public CardData produce(Portfolio portfolio, AssetDisposalCard card) {
        var now = LocalDate.now();
        var trackedYear = Year.of(now.getYear());
        var positions = portfolioPositionService.findPositionsWithSellTradesByYear(portfolio.getId(), trackedYear);
        var tickerByTradePnls = positions.stream()
            .collect(Collectors.toMap(p -> p.getInstrument().toTicker(), p -> {
                var aggregatedTrade = tradeAggregatorService.aggregateTradesByPositionId(p.getId(), now);
                return aggregatedTrade.getDeltaPnls();
            }));
        var portfolioCurrency = portfolio.getCurrency();
        var profitsByLosses = tickerByTradePnls.entrySet().stream()
            .map(tickerByPnl -> tickerByPnl.getValue().stream()
                .map(pnl -> new AssetDisposalDetails(tickerByPnl.getKey(), pnl.calculatePnl(currencyConverter::convert, portfolioCurrency)))
                .toList()
            )
            .flatMap(Collection::stream)
            .collect(Collectors.partitioningBy(details -> details.total().compareTo(BigDecimal.ZERO) > 0,
                Collectors.toMap(AssetDisposalDetails::ticker,
                    Function.identity(),
                    (d1, d2) -> new AssetDisposalDetails(d1.ticker(), d1.total().add(d2.total())))));
        var profitDetails = profitsByLosses.get(Boolean.TRUE);
        var profits = calculateSumTotal(portfolioCurrency, profitDetails);
        var lossDetails = profitsByLosses.get(Boolean.FALSE);
        var loses = calculateSumTotal(portfolioCurrency, lossDetails);
        var taxableIncome = loses.add(profits);

        return new AssetDisposalCardData()
            .setProfits(profits)
            .setProfitDetails(profitDetails.values())
            .setLosses(loses)
            .setLossDetails(lossDetails.values())
            .setTaxableIncome(taxableIncome)
            .setTrackedYears(Collections.singletonList(trackedYear))
            .setCurrencyCode(portfolioCurrency.getCurrencyCode());
    }

    private BigDecimal calculateSumTotal(Currency portfolioCurrency, Map<Ticker, AssetDisposalDetails> detailsByTicker) {
        return detailsByTicker.values()
            .stream()
            .map(AssetDisposalDetails::total)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
