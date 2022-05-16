package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionCardData;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionDataItem;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.trades.domain.security.SecurityAggregatedTrade;
import com.topably.assets.trades.service.SecurityTradeService;
import com.topably.assets.xrates.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Service(CardContainerType.SECTORAL_DISTRIBUTION)
@RequiredArgsConstructor
public class SectoralDistributionCardStateProducer implements CardStateProducer<SectoralDistributionCard> {

    private final SecurityTradeService securityTradeService;
    private final StockService stockService;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public PortfolioCardData produce(Principal user, SectoralDistributionCard card) {
        var aggregatedTrades = securityTradeService.findUserAggregatedStockTrades(user.getName());
        var stockIdByTrade = aggregatedTrades.stream().collect(toMap(SecurityAggregatedTrade::getSecurityId, Function.identity()));

        return SectoralDistributionCardData.builder()
                .items(composeDataItems(stockIdByTrade))
                .build();
    }

    private Collection<SectoralDistributionDataItem> composeDataItems(Map<Long, SecurityAggregatedTrade> stockIdByTrade) {
        var stocks = stockService.findAllById(stockIdByTrade.keySet());
        var companyNameByStockIds = stocks.stream().collect(groupingBy(s -> s.getCompany().getName(),
                mapping(Instrument::getId, toSet())));
        var companyGroupings = stocks.stream()
                .filter(s -> s.getCompany().getSubIndustry() != null)
                .collect(groupingBy(stock -> stock.getCompany().getSubIndustry().getGroup().getName(),
                        groupingBy(s -> s.getCompany().getSubIndustry().getName(), mapping(s -> s.getCompany().getName(), toSet()))));

        var items = new ArrayList<SectoralDistributionDataItem>();
        companyGroupings.forEach((group, rest) -> {
            var groupBuilder = SectoralDistributionDataItem.builder()
                    .name(group);
            var groupChildren = new ArrayList<SectoralDistributionDataItem>();
            rest.forEach((industry, names) -> {
                var children = names.stream()
                        .map(name -> {
                            BigDecimal total = companyNameByStockIds.get(name).stream()
                                    .map(stockIdByTrade::get)
                                    .map(trade -> currencyService.convert(trade.getTotal(), trade.getCurrency()))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return composeLeafItem(name, total);
                        })
                        .collect(toList());
                var value = children.stream().map(SectoralDistributionDataItem::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                groupChildren.add(SectoralDistributionDataItem.builder()
                        .name(industry)
                        .value(value)
                        .children(children)
                        .build());
            });
            var value = groupChildren.stream().map(SectoralDistributionDataItem::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            items.add(groupBuilder.value(value)
                    .children(groupChildren)
                    .build());
        });
        return items;
    }

    private SectoralDistributionDataItem composeLeafItem(String name, BigDecimal total) {
        return SectoralDistributionDataItem.builder()
                .name(name)
                .value(total)
                .build();
    }
}
