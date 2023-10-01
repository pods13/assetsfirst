package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.companies.util.CompanyUtils;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionCardData;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionDataItem;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
    private final PortfolioPositionService portfolioPositionService;
    private final ExchangeService exchangeService;

    @Override
    public CardData produce(Portfolio portfolio, SectoralDistributionCard card) {
        var positionDtos = portfolioPositionService.findPortfolioPositions(portfolio.getId())
            .stream().filter(dto -> InstrumentType.STOCK.name().equals(dto.getInstrumentType()))
            .toList();
        var stockIdByPosition = positionDtos.stream()
            .collect(toMap(PortfolioPositionDto::getInstrumentId, Function.identity()));

        return SectoralDistributionCardData.builder()
            .items(composeDataItems(portfolio, stockIdByPosition))
            .build();
    }

    private Collection<SectoralDistributionDataItem> composeDataItems(Portfolio portfolio, Map<Long, PortfolioPositionDto> stockIdByPosition) {
        var stocks = stockService.findAllById(stockIdByPosition.keySet());
        var companyNameByStockIds = stocks.stream()
            .collect(groupingBy(CompanyUtils::resolveCompanyName, mapping(Instrument::getId, toSet())));

        var items = new TreeSet<SectoralDistributionDataItem>();
        buildDistributionTree(stocks).forEach((group, rest) -> {
            var groupBuilder = SectoralDistributionDataItem.builder()
                .name(group);
            var groupChildren = new TreeSet<SectoralDistributionDataItem>();
            rest.forEach((industry, names) -> {
                var children = names.stream()
                    .map(name -> {
                        var total = calculateTotalPerCompany(portfolio, companyNameByStockIds.get(name), stockIdByPosition);
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
            var value = groupChildren.stream()
                .map(SectoralDistributionDataItem::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            items.add(groupBuilder.value(value)
                .children(groupChildren)
                .build());
        });
        return items;
    }

    private static Map<String, Map<String, Set<String>>> buildDistributionTree(Collection<Stock> stocks) {
        return stocks.stream()
            .filter(s -> s.getCompany().getIndustry() != null)
            .collect(groupingBy(stock -> stock.getCompany().getIndustry().getSector().getName(),
                groupingBy(s -> s.getCompany().getIndustry().getName(), mapping(s -> s.getCompany().getName(), toSet()))));
    }

    private BigDecimal calculateTotalPerCompany(Portfolio portfolio, Set<Long> stockIds, Map<Long, PortfolioPositionDto> stockIdByPosition) {
        return stockIds.stream()
            .map(stockIdByPosition::get)
            .map(position -> {
                var price = exchangeService.findSymbolRecentPrice(position.getIdentifier())
                    .orElse(position.getPrice());
                var positionTotal = price.multiply(new BigDecimal(position.getQuantity()));
                return currencyConverterService.convert(positionTotal, position.getCurrency(), portfolio.getCurrency());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private SectoralDistributionDataItem composeLeafItem(String name, BigDecimal total) {
        return SectoralDistributionDataItem.builder()
            .name(name)
            .value(total)
            .build();
    }
}
