package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.domain.security.SecurityAggregatedTrade;
import com.topably.assets.trades.service.SecurityTradeService;
import com.topably.assets.xrates.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Currency;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service(CardContainerType.ALLOCATION)
@RequiredArgsConstructor
public class AllocationCardStateProducer implements CardStateProducer<AllocationCard> {

    private static final Currency DESTINATION_CURRENCY = Currency.getInstance("RUB");

    private final SecurityTradeService tradeService;
    private final ExchangeRateService exchangeRateService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, AllocationCard card) {
        List<AllocationSegment> segments = tradeService.findUserAggregatedTrades(user.getName()).stream()
                .map(this::convertToSegment)
                .collect(toList());
        return AllocationCardData.builder()
                .segments(segments)
                .totalInvested(calculateTotalInvested(segments))
                .build();
    }

    private AllocationSegment convertToSegment(SecurityAggregatedTrade trade) {
        var name = trade.getIdentifier().toString();
        var price = exchangeRateService.convertCurrency(trade.getTotal(), trade.getCurrency(), DESTINATION_CURRENCY);
        return new AllocationSegment(name, price);
    }

    private BigDecimal calculateTotalInvested(List<AllocationSegment> segments) {
        return segments.stream()
                .map(AllocationSegment::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
