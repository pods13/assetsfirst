package com.topably.assets.portfolios.service.cards.producer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverter;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentTag;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionCardData;
import com.topably.assets.portfolios.domain.cards.output.SectoralDistributionDataItem;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import com.topably.assets.tags.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.topably.assets.instruments.domain.InstrumentTag.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;


@Service(CardContainerType.SECTORAL_DISTRIBUTION)
@RequiredArgsConstructor
public class SectoralDistributionCardStateProducer implements CardStateProducer<SectoralDistributionCard> {

    private final CurrencyConverter currencyConverter;
    private final PortfolioPositionService portfolioPositionService;
    private final ExchangeService exchangeService;
    private final InstrumentService instrumentService;

    @Override
    public CardData produce(Portfolio portfolio, SectoralDistributionCard card) {
        var positionDtos = portfolioPositionService.findPortfolioPositions(portfolio.getId());
        var instrumentIdByPosition = positionDtos.stream()
            .collect(toMap(PortfolioPositionDto::getInstrumentId, Function.identity()));

        return SectoralDistributionCardData.builder()
            .items(composeDataItems(portfolio, instrumentIdByPosition))
            .build();
    }

    private Collection<SectoralDistributionDataItem> composeDataItems(Portfolio portfolio, Map<Long, PortfolioPositionDto> instrumentIdByPosition) {
        var instruments = instrumentService.findAllById(instrumentIdByPosition.keySet());
        var instumentNameById = instruments.stream()
            .collect(groupingBy(Instrument::getName, mapping(Instrument::getId, toSet())));

        var items = new TreeSet<SectoralDistributionDataItem>();
        buildDistributionTree(instruments).forEach((group, rest) -> {
            var groupBuilder = SectoralDistributionDataItem.builder()
                .name(group);
            var groupChildren = new TreeSet<SectoralDistributionDataItem>();
            rest.forEach((industry, names) -> {
                var children = names.stream()
                    .map(name -> {
                        var total = calculateTotalPerInstrument(portfolio, instumentNameById.get(name), instrumentIdByPosition);
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

    private static Map<String, Map<String, Set<String>>> buildDistributionTree(Collection<Instrument> instruments) {
        return instruments.stream()
            .filter(i -> i.getTags().stream().anyMatch(t -> t.getCategory().getCode().equals("sector")))
            .collect(groupingBy(i -> i.getTags().stream().filter(t -> t.getCategory().getCode().equals(SECTOR_TAG_CATEGORY)).findFirst().map(Tag::getName).orElse(""),
                groupingBy(i -> i.getTags().stream().filter(t -> t.getCategory().getCode().equals(INDUSTRY_TAG_CATEGORY)).findFirst().map(Tag::getName).orElse(""),
                    mapping(Instrument::getName, toSet()))));
    }

    private BigDecimal calculateTotalPerInstrument(
        Portfolio portfolio,
        Set<Long> instrumentIds,
        Map<Long, PortfolioPositionDto> instrumentIdByPosition
    ) {
        return instrumentIds.stream()
            .map(instrumentIdByPosition::get)
            .map(position -> {
                var price = exchangeService.findSymbolRecentPrice(position.getIdentifier())
                    .orElse(position.getPrice());
                var positionTotal = price.multiply(new BigDecimal(position.getQuantity()));
                return currencyConverter.convert(positionTotal, position.getCurrency(), portfolio.getCurrency());
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
