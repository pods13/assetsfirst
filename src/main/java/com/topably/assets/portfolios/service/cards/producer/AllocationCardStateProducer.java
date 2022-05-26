package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private final ExchangeService exchangeService;
    private final CurrencyService currencyService;

    private final PortfolioService portfolioService;
    private final PortfolioHoldingService portfolioHoldingService;

    @Override
    @Transactional
    public CardData produce(Principal user, AllocationCard card) {
        var portfolio = portfolioService.findByUsername(user.getName());
        var portfolioHoldingDtos = portfolioHoldingService.findPortfolioHoldings(portfolio.getId());
        var segments = portfolioHoldingDtos.stream()
                .map(this::convertToSegment)
                .sorted(Comparator.comparing(AllocationSegment::getValue).reversed())
                .collect(toList());
        return AllocationCardData.builder()
                .segments(segments)
                .investedValue(calculateInvestedValue(segments))
                .currentValue(calculateCurrentValue(portfolioHoldingDtos))
                .build();
    }

    private AllocationSegment convertToSegment(PortfolioHoldingDto trade) {
        var name = trade.getIdentifier().toString();
        var price = currencyService.convert(trade.getTotal(), trade.getCurrency());
        return new AllocationSegment(name, price);
    }

    private BigDecimal calculateInvestedValue(List<AllocationSegment> segments) {
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
