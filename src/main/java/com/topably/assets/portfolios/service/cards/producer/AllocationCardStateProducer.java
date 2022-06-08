package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.allocation.AggregatedTrade;
import com.topably.assets.portfolios.domain.cards.input.allocation.AggregatedTradeCollector;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocatedByOption;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
@Slf4j
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;

    private final PortfolioHoldingService portfolioHoldingService;
    private final TradeService tradeService;

    @Override
    public CardData produce(Portfolio portfolio, AllocationCard card) {
        var tradesByType = collectAggregatedTrades(portfolio, card);
        var segments = tradesByType.entrySet().stream()
                .map(byType -> {
                    var childSegments = convertToSegments(byType.getValue());
                    return AllocationSegment.builder().name(byType.getKey())
                            .value(calculateSegmentsTotalValue(childSegments))
                            .children(childSegments)
                            .build();
                }).toList();
        List<AggregatedTrade> aggregatedTrades = tradesByType.values().stream().flatMap(Collection::stream).toList();
        return AllocationCardData.builder()
                .segments(segments)
                .investedValue(calculateSegmentsTotalValue(segments))
                .currentValue(calculateCurrentValue(aggregatedTrades))
                .build();
    }

    private Map<String, List<AggregatedTrade>> collectAggregatedTrades(Portfolio portfolio, AllocationCard card) {
        var allocatedBy = card.getAllocatedBy();
        if (AllocatedByOption.INSTRUMENT_TYPE.equals(allocatedBy)) {
            return portfolioHoldingService.findPortfolioHoldings(portfolio.getId()).stream()
                    .map(holding -> AggregatedTrade.builder()
                            .identifier(holding.getIdentifier())
                            .instrumentId(holding.getInstrumentId())
                            .instrumentType(holding.getInstrumentType())
                            .price(holding.getPrice())
                            .quantity(holding.getQuantity())
                            .currency(holding.getCurrency())
                            .build())
                    .collect(groupingBy(AggregatedTrade::getInstrumentType));
        } else if (AllocatedByOption.BROKER.equals(allocatedBy)) {
            Long userId = portfolio.getUser().getId();
            return tradeService.findTradesByUserId(userId).stream()
                    .map(t -> {
                        PortfolioHolding holding = t.getPortfolioHolding();
                        return AggregatedTrade.builder()
                                .identifier(holding.getInstrument().toTickerSymbol())
                                .instrumentId(holding.getInstrument().getId())
                                .instrumentType(holding.getInstrument().getInstrumentType())
                                .price(t.getPrice())
                                .quantity(TradeOperation.SELL.equals(t.getOperation()) ? t.getQuantity().negate() : t.getQuantity())
                                .currency(holding.getInstrument().getExchange().getCurrency())
                                .brokerName(t.getBroker().getName())
                                .build();
                    })
                    .collect(groupingBy(AggregatedTrade::getBrokerName, collectingAndThen(toList(),
                            trades -> trades.stream().collect(new AggregatedTradeCollector()))));
        }
        return Collections.emptyMap();
    }

    private List<AllocationSegment> convertToSegments(Collection<AggregatedTrade> trades) {
        return trades.stream()
                .map(this::convertToSegment)
                .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
                .collect(toList());
    }

    private AllocationSegment convertToSegment(AggregatedTrade trade) {
        var name = trade.getIdentifier().toString();
        var price = currencyConverterService.convert(trade.getTotal(), trade.getCurrency());
        log.info("{} {} {} {}", name, price, trade.getPrice(), trade.getQuantity());
        return AllocationSegment.builder()
                .name(name)
                .value(price)
                .build();
    }

    private BigDecimal calculateSegmentsTotalValue(List<AllocationSegment> segments) {
        return segments.stream()
                .map(AllocationSegment::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateCurrentValue(Collection<AggregatedTrade> trades) {
        return trades.stream()
                .map(t -> exchangeService.findTickerRecentPrice(t.getIdentifier())
                        .map(value -> value.multiply(new BigDecimal(t.getQuantity())))
                        .map(total -> currencyConverterService.convert(total, t.getCurrency()))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_EVEN);
    }
}
