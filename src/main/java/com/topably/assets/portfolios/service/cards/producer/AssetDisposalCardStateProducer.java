package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.AssetDisposalCard;
import com.topably.assets.portfolios.domain.cards.output.AssetDisposalCardData;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service(CardContainerType.ASSET_DISPOSAL)
@RequiredArgsConstructor
@Slf4j
public class AssetDisposalCardStateProducer implements CardStateProducer<AssetDisposalCard> {

    private final PortfolioPositionService portfolioPositionService;
    private final TradeAggregatorService tradeAggregatorService;
    private final CurrencyConverterService currencyConverterService;

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
        var profitsByLosses = tickerByTradePnls.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.partitioningBy(deltaPnl -> {
                var totalBuy = currencyConverterService.convert(deltaPnl.totalBuy(), deltaPnl.currency(), portfolioCurrency,
                    deltaPnl.buyDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                var totalSell = currencyConverterService.convert(deltaPnl.totalSell(), deltaPnl.currency(), portfolioCurrency,
                    deltaPnl.sellDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                return totalSell.subtract(totalBuy).compareTo(BigDecimal.ZERO) > 0;
            }));
        var profits = calculateSumTotal(portfolioCurrency, profitsByLosses.get(Boolean.TRUE));
        var loses = calculateSumTotal(portfolioCurrency, profitsByLosses.get(Boolean.FALSE));
        var taxableIncome = loses.add(profits);
        return new AssetDisposalCardData()
            .setProfits(Collections.singletonList(profits))
            .setLosses(Collections.singletonList(loses))
            .setTaxableIncome(Collections.singletonList(taxableIncome))
            .setTrackedYears(Collections.singletonList(trackedYear))
            .setCurrencyCode(portfolioCurrency.getCurrencyCode());
    }

    private BigDecimal calculateSumTotal(Currency portfolioCurrency, List<AggregatedTradeDto.DeltaPnl> deltaPnls) {
        return deltaPnls.stream()
            .map(deltaPnl -> {
                var totalBuy = currencyConverterService.convert(deltaPnl.totalBuy(), deltaPnl.currency(), portfolioCurrency,
                    deltaPnl.buyDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                var totalSell = currencyConverterService.convert(deltaPnl.totalSell(), deltaPnl.currency(), portfolioCurrency,
                    deltaPnl.sellDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                return totalSell.subtract(totalBuy);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
