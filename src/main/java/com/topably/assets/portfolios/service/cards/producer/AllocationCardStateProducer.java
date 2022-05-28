package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.DashboardCard;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.xrates.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private final ExchangeService exchangeService;
    private final CurrencyService currencyService;

    private final PortfolioHoldingService portfolioHoldingService;

    @Override
    public CardData produce(Portfolio portfolio, AllocationCard card) {
        var holdings = portfolioHoldingService.findPortfolioHoldings(portfolio.getId());
        var holdingByTypes = holdings.stream()
                .collect(groupingBy(PortfolioHoldingDto::getInstrumentType));
        var segments = holdingByTypes.entrySet().stream()
                .map(byType -> {
                    var childSegments = convertToSegments(byType.getValue());
                    return AllocationSegment.builder().name(byType.getKey())
                            .value(calculateSegmentsTotalValue(childSegments))
                            .children(childSegments)
                            .build();
                }).toList();
        return AllocationCardData.builder()
                .segments(segments)
                .investedValue(calculateSegmentsTotalValue(segments))
                .currentValue(calculateCurrentValue(holdings))
                .build();
    }

    private List<AllocationSegment> convertToSegments(Collection<PortfolioHoldingDto> holdings) {
        return holdings.stream()
                .map(this::convertToSegment)
                .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
                .collect(toList());
    }

    private AllocationSegment convertToSegment(PortfolioHoldingDto holding) {
        var name = holding.getIdentifier().toString();
        var price = currencyService.convert(holding.getTotal(), holding.getCurrency());
        return AllocationSegment.builder()
                .name(name)
                .value(price)
                .build();
    }

    private BigDecimal calculateSegmentsTotalValue(List<AllocationSegment> segments) {
        return segments.stream()
                .map(AllocationSegment::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateCurrentValue(Collection<PortfolioHoldingDto> portfolioHoldingDtos) {
        return portfolioHoldingDtos.stream()
                .map(t -> exchangeService.findTickerRecentPrice(t.getIdentifier())
                        .map(value -> value.multiply(new BigDecimal(t.getQuantity())))
                        .map(total -> currencyService.convert(total, t.getCurrency()))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
