package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionCardData;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionDataItem;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.service.PortfolioHoldingService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service(CardContainerType.SECTORAL_DISTRIBUTION)
@RequiredArgsConstructor
public class SectoralDistributionCardStateProducer implements CardStateProducer<SectoralDistributionCard> {

    private final StockService stockService;
    private final CurrencyConverterService currencyConverterService;

    private final PortfolioHoldingService portfolioHoldingService;

    @Override
    public CardData produce(Portfolio portfolio, SectoralDistributionCard card) {
        var holdingDtos = portfolioHoldingService.findPortfolioHoldings(portfolio.getId())
                .stream().filter(dto -> InstrumentType.STOCK.name().equals(dto.getInstrumentType()))
                .toList();
        var stockIdByTrade = holdingDtos.stream().collect(toMap(PortfolioHoldingDto::getInstrumentId, Function.identity()));

        return SectoralDistributionCardData.builder()
                .items(composeDataItems(stockIdByTrade))
                .build();
    }

    private Collection<SectoralDistributionDataItem> composeDataItems(Map<Long, PortfolioHoldingDto> stockIdByTrade) {
        var stocks = stockService.findAllById(stockIdByTrade.keySet());
        var companyNameByStockIds = stocks.stream()
                .collect(groupingBy(s -> s.getCompany().getName(), mapping(Instrument::getId, toSet())));
        var companyGroupings = stocks.stream()
                .filter(s -> s.getCompany().getIndustry() != null)
                .collect(groupingBy(stock -> stock.getCompany().getIndustry().getSector().getName(),
                        groupingBy(s -> s.getCompany().getIndustry().getName(), mapping(s -> s.getCompany().getName(), toSet()))));

        var items = new TreeSet<SectoralDistributionDataItem>();
        companyGroupings.forEach((group, rest) -> {
            var groupBuilder = SectoralDistributionDataItem.builder()
                    .name(group);
            var groupChildren = new TreeSet<SectoralDistributionDataItem>();
            rest.forEach((industry, names) -> {
                var children = names.stream()
                        .map(name -> {
                            BigDecimal total = companyNameByStockIds.get(name).stream()
                                    .map(stockIdByTrade::get)
                                    .map(trade -> currencyConverterService.convert(trade.getTotal(), trade.getCurrency()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            return composeLeafItem(name, total);
                        })
                        .collect(Collectors.toCollection(TreeSet::new));
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
