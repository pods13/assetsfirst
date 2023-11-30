package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.AssetDisposalCard;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocatedByOption;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTrade;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationAggregatedTradeCollector;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.input.allocation.CustomSegment;
import com.topably.assets.portfolios.domain.cards.input.allocation.TagWithCategoryDto;
import com.topably.assets.portfolios.domain.cards.output.AllocationCardData;
import com.topably.assets.portfolios.domain.cards.output.AllocationSegment;
import com.topably.assets.portfolios.domain.cards.output.AssetDisposalCardData;
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

@Service(CardContainerType.ASSET_DISPOSAL)
@RequiredArgsConstructor
@Slf4j
public class AssetDisposalCardStateProducer implements CardStateProducer<AssetDisposalCard> {

    private final PortfolioPositionService portfolioPositionService;
    private final TradeAggregatorService tradeAggregatorService;

    @Override
    public CardData produce(Portfolio portfolio, AssetDisposalCard card) {

        /**
         * TODO
         * find positions that has at least one sell trade at a specific year(use this year only for now)
         * find trades related to this position ticker at a specific year
         * calculate pnl for them
         */
        return new AssetDisposalCardData();
    }
}
