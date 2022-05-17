package com.topably.assets.trades.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.TradeView;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.AggregatedTrade;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.repository.TradeRepository;
import com.topably.assets.trades.repository.TradeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final UserService userService;
    private final TradeRepository tradeRepository;
    private final TradeViewRepository tradeViewRepository;

    @Override
    public Collection<Trade> findUserTrades(String username) {
        return tradeRepository.findAllByUser_Username(username);
    }

    @Override
    public Collection<Trade> findUserDividendPayingTrades(String username) {
        return tradeRepository.findUserDividendPayingTradesOrderByTradeDate(username);
    }

    @Override
    @Transactional
    public Collection<AggregatedTrade> findUserAggregatedTrades(String username) {
        return aggregateTrades(findUserTrades(username));
    }

    @Override
    @Transactional
    public Collection<AggregatedTrade> findUserAggregatedStockTrades(String username) {
        String instrumentType = InstrumentType.STOCK.name();
        Collection<Trade> trades = tradeRepository.findAllByUser_UsernameAndInstrument_InstrumentType(username, instrumentType);
        return aggregateTrades(trades);

    }

    private Collection<AggregatedTrade> aggregateTrades(Collection<Trade> trades) {
        var groupedTrades = trades.stream()
                .collect(groupingBy(trade -> {
                    Instrument instrument = trade.getInstrument();
                    return new TickerSymbol(instrument.getTicker(), instrument.getExchange().getCode());
                }));
        return groupedTrades.entrySet().stream()
                .map(entry -> aggregateTrades(entry.getKey(), entry.getValue()))
                .filter(aggregatedTrade -> aggregatedTrade.getTotal().compareTo(BigDecimal.ZERO) != 0)
                .collect(toList());
    }

    private AggregatedTrade aggregateTrades(TickerSymbol key, List<Trade> trades) {
        BigDecimal total = trades.stream()
                .map(this::calculateTradeTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigInteger quantity = trades.stream().map(this::calculateTotalQuantity).reduce(BigInteger.ZERO, BigInteger::add);
        Iterator<Trade> tradeIterator = trades.iterator();
        var tradedInstrument = tradeIterator.hasNext()
                ? Optional.of(tradeIterator.next().getInstrument())
                : Optional.<Instrument>empty();
        return AggregatedTrade.builder()
                .instrumentId(tradedInstrument.map(Instrument::getId).orElse(null))
                .identifier(key)
                .total(total)
                .currency(tradedInstrument.map(s -> s.getExchange().getCurrency()).orElse(null))
                .quantity(quantity)
                .build();
    }

    private BigDecimal calculateTradeTotal(Trade trade) {
        BigDecimal total = trade.getPrice().multiply(new BigDecimal(trade.getQuantity()));
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return total.negate();
        }
        return total;
    }

    private BigInteger calculateTotalQuantity(Trade trade) {
        BigInteger quantity = trade.getQuantity();
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return quantity.negate();
        }
        return quantity;
    }

    @Override
    @Transactional
    public TradeDto addTrade(AddTradeDto dto, String username, Instrument tradedInstrument) {
        User user = userService.findByUsername(username);
        var trade = Trade.builder()
                .instrument(tradedInstrument)
                .operation(dto.getOperation())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .user(user)
                .build();
        var savedTrade = tradeRepository.save(trade);
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
    }

    @Override
    public Collection<TradeView> getUserTrades(String username) {
        return tradeViewRepository.findByUsername(username);
    }
}
