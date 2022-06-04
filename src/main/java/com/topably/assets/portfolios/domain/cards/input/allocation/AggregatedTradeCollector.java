package com.topably.assets.portfolios.domain.cards.input.allocation;

import com.topably.assets.core.domain.TickerSymbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AggregatedTradeCollector implements Collector<AggregatedTrade, Map<TickerSymbol, AggregatedTrade>, List<AggregatedTrade>> {

    @Override
    public Supplier<Map<TickerSymbol, AggregatedTrade>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<TickerSymbol, AggregatedTrade>, AggregatedTrade> accumulator() {
        return (map, trade) -> {
            TickerSymbol key = trade.getIdentifier();
            if (map.containsKey(key)) {
                AggregatedTrade prevTrade = map.get(key);
                var totalQuantity = prevTrade.getQuantity().add(trade.getQuantity());
                BigDecimal averagePrice = prevTrade.getTotal().add(trade.getTotal())
                        .divide(new BigDecimal(totalQuantity), 4, RoundingMode.HALF_UP);
                prevTrade.setQuantity(totalQuantity);
                prevTrade.setPrice(averagePrice);
            } else {
                map.put(trade.getIdentifier(), trade);
            }
        };
    }

    @Override
    public BinaryOperator<Map<TickerSymbol, AggregatedTrade>> combiner() {
        return (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        };
    }

    @Override
    public Function<Map<TickerSymbol, AggregatedTrade>, List<AggregatedTrade>> finisher() {
        return map -> new ArrayList<>(map.values());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Collector.Characteristics.UNORDERED);
    }
}
