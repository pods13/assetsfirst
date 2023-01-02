package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocatedByOption;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTrade;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTradeCollector;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
@Slf4j
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;

    private final PortfolioPositionService portfolioPositionService;
    private final TradeAggregatorService tradeAggregatorService;

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
        List<AllocationAggregatedTrade> aggregatedTrades = tradesByType.values().stream().flatMap(Collection::stream).toList();
        return AllocationCardData.builder()
            .segments(segments)
            .currentTotalValue(calculateSegmentsTotalValue(segments))
            .build();
    }

    private Map<String, List<AllocationAggregatedTrade>> collectAggregatedTrades(Portfolio portfolio, AllocationCard card) {
        var allocatedBy = card.getAllocatedBy();
        if (AllocatedByOption.BROKER.equals(allocatedBy)) {
            return aggregateTradesByBroker(portfolio);
        }
        return portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
            .map(position -> {
                var price = exchangeService.findSymbolRecentPrice(position.getIdentifier())
                    .orElse(position.getPrice());
                return AllocationAggregatedTrade.builder()
                    .identifier(position.getIdentifier())
                    .instrumentId(position.getInstrumentId())
                    .instrumentType(position.getInstrumentType())
                    .price(price)
                    .quantity(position.getQuantity())
                    .currency(position.getCurrency())
                    .build();
            })
            .collect(groupingBy(getAllocatedByClassifier(allocatedBy)));
    }

    private Map<String, List<AllocationAggregatedTrade>> aggregateTradesByBroker(Portfolio portfolio) {
        var positionIds = portfolioPositionService.findAllPositionIdsByPortfolioId(portfolio.getId());
        return positionIds.stream()
            .map(tradeAggregatorService::aggregateTradesByPositionId)
            .map(AggregatedTradeDto::getBuyTradesData)
            .flatMap(Collection::stream)
            .collect(groupingBy(AggregatedTradeDto.TradeData::getBrokerName, collectingAndThen(toList(),
                trades -> trades.stream().map(t -> {
                    var price = exchangeService.findSymbolRecentPrice(t.getTicker()).orElse(t.getPrice());
                    return AllocationAggregatedTrade.builder()
                        .identifier(t.getTicker())
                        .instrumentId(t.getInstrumentId())
                        .instrumentType(t.getInstrumentType())
                        .price(price)
                        .quantity(t.getShares())
                        .currency(t.getCurrency())
                        .build();
                }).collect(new AllocationAggregatedTradeCollector()))));
    }

    private Function<AllocationAggregatedTrade, String> getAllocatedByClassifier(AllocatedByOption allocatedBy) {
        if (AllocatedByOption.INSTRUMENT_TYPE.equals(allocatedBy)) {
            return AllocationAggregatedTrade::getInstrumentType;
        } else if (AllocatedByOption.TRADING_CURRENCY.equals(allocatedBy)) {
            return t -> t.getCurrency().getCurrencyCode();
        }
        throw new RuntimeException();
    }

    private List<AllocationSegment> convertToSegments(Collection<AllocationAggregatedTrade> trades) {
        return trades.stream()
            .map(this::convertToSegment)
            .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
            .collect(toList());
    }

    private AllocationSegment convertToSegment(AllocationAggregatedTrade trade) {
        var name = trade.getIdentifier().toString();
        var price = currencyConverterService.convert(trade.getTotal(), trade.getCurrency());
        return AllocationSegment.builder()
            .name(name)
            .value(price)
            .build();
    }

    private BigDecimal calculateSegmentsTotalValue(List<AllocationSegment> segments) {
        return segments.stream()
            .map(AllocationSegment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
