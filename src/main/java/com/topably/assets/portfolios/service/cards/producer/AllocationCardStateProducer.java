package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverter;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocatedByOption;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTrade;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTradeCollector;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.input.allocation.CustomSegment;
import com.topably.assets.portfolios.domain.cards.input.allocation.TagWithCategoryDto;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
@Slf4j
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private final ExchangeService exchangeService;
    private final CurrencyConverter currencyConverter;

    private final PortfolioPositionService portfolioPositionService;
    private final TradeAggregatorService tradeAggregatorService;

    @Override
    public CardData produce(Portfolio portfolio, AllocationCard card) {
        var tradesByType = collectAggregatedTrades(portfolio, card);
        var portfolioCurrency = portfolio.getCurrency();
        var segments = tradesByType.entrySet().stream()
            .map(byType -> {
                var childSegments = convertToSegments(portfolioCurrency, byType.getValue());
                return AllocationSegment.builder().name(byType.getKey())
                    .value(calculateSegmentsTotalValue(childSegments))
                    .currencyCode(portfolioCurrency.getCurrencyCode())
                    .children(childSegments)
                    .build();
            })
            .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
            .toList();
        return AllocationCardData.builder()
            .segments(segments)
            .currentTotalValue(calculateSegmentsTotalValue(segments))
            .build();
    }

    private Map<String, List<AllocationAggregatedTrade>> collectAggregatedTrades(Portfolio portfolio, AllocationCard card) {
        var allocatedBy = card.getAllocatedBy();
        if (AllocatedByOption.BROKER.equals(allocatedBy)) {
            return aggregateTradesByBroker(portfolio);
        } else if (AllocatedByOption.CUSTOM.equals(allocatedBy)) {
            return card.getCustomSegments().stream()
                .collect(Collectors.toMap(CustomSegment::getName, s -> {
                    var tagIds = s.getTags().stream().map(TagWithCategoryDto::id).collect(Collectors.toSet());
                    return portfolioPositionService.findPortfolioPositionsByPortfolioIdAndTags(portfolio.getId(), tagIds).stream()
                        .map(this::mapPositionToAllocationAggregatedTrade)
                        .toList();
                }));
        }
        return portfolioPositionService.findPortfolioPositions(portfolio.getId()).stream()
            .map(this::mapPositionToAllocationAggregatedTrade)
            .collect(groupingBy(getAllocatedByClassifier(allocatedBy)));
    }

    private AllocationAggregatedTrade mapPositionToAllocationAggregatedTrade(PortfolioPositionDto position) {
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

    private List<AllocationSegment> convertToSegments(Currency portfolioCurrency, Collection<AllocationAggregatedTrade> trades) {
        return trades.stream()
            .filter(trade -> trade.getTotal().compareTo(BigDecimal.ZERO) > 0)
            .map(trade -> convertToSegment(portfolioCurrency, trade))
            .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
            .collect(toList());
    }

    private AllocationSegment convertToSegment(Currency portfolioCurrency, AllocationAggregatedTrade trade) {
        var name = trade.getIdentifier().toString();
        var price = currencyConverter.convert(trade.getTotal(), trade.getCurrency(), portfolioCurrency);
        return AllocationSegment.builder()
            .name(name)
            .value(price)
            .currencyCode(portfolioCurrency.getCurrencyCode())
            .build();
    }

    private BigDecimal calculateSegmentsTotalValue(List<AllocationSegment> segments) {
        return segments.stream()
            .map(AllocationSegment::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
