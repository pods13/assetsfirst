package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.output.BalanceCardData;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.trades.service.TradeService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

@Service(CardContainerType.BALANCE)
@RequiredArgsConstructor
public class BalanceCardStateProducer implements CardStateProducer<BalanceCard> {

    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;
    private final PortfolioHoldingService portfolioHoldingService;
    private final TradeService tradeService;

    @Override
    public CardData produce(Portfolio portfolio, BalanceCard card) {
        Collection<PortfolioHoldingDto> holdings = portfolioHoldingService.findPortfolioHoldings(portfolio.getId());
        return BalanceCardData.builder()
                .investedAmount(calculateInvestedAmount(holdings))
                .currentAmount(calculateCurrentAmount(holdings))
                //TODO use portfolio currency instead
                .currencySymbol(Currency.getInstance("RUB").getSymbol())
                .build();
    }

    private BigDecimal calculateCurrentAmount(Collection<PortfolioHoldingDto> holdings) {
        return holdings.stream()
                .map(h -> {
                    var marketValue = exchangeService.findSymbolRecentPrice(h.getIdentifier())
                            .map(value -> value.multiply(new BigDecimal(h.getQuantity())))
                            .orElse(h.getTotal());
                    return currencyConverterService.convert(marketValue, h.getCurrency());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateInvestedAmount(Collection<PortfolioHoldingDto> holdings) {
        Currency portfolioCurrency = Currency.getInstance("RUB");
        return holdings.stream()
                .map(h -> tradeService.calculateInvestedAmountByHoldingId(h.getId(), h.getCurrency(), portfolioCurrency))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
