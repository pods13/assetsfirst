package com.topably.assets.trades.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.security.SecurityAggregatedTrade;
import com.topably.assets.trades.domain.security.SecurityTrade;
import com.topably.assets.trades.repository.SecurityTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SecurityTradeServiceImpl implements SecurityTradeService {

    private final UserService userService;
    private final SecurityTradeRepository tradeRepository;

    @Override
    public Collection<SecurityTrade> findUserTrades(String username) {
        return tradeRepository.findAllByUser_Username(username);
    }

    @Override
    public Collection<SecurityTrade> findUserDividendPayingTrades(String username) {
        return tradeRepository.findUserDividendPayingTradesOrderByTradeDate(username);
    }

    @Override
    @Transactional
    public Collection<SecurityAggregatedTrade> findUserAggregatedTrades(String username) {
        var groupedTrades = findUserTrades(username).stream()
                .collect(groupingBy(trade -> {
                    Security security = trade.getSecurity();
                    return new TickerSymbol(security.getTicker(), security.getExchange().getCode());
                }));
        return groupedTrades.entrySet().stream()
                .map(entry -> aggregateTrades(entry.getKey(), entry.getValue()))
                .filter(aggregatedTrade -> aggregatedTrade.getTotal().compareTo(BigDecimal.ZERO) != 0)
                .collect(toList());
    }

    private SecurityAggregatedTrade aggregateTrades(TickerSymbol key, List<SecurityTrade> trades) {
        BigDecimal total = trades.stream()
                .map(this::calculateTradeTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigInteger quantity = trades.stream().map(this::calculateTotalQuantity).reduce(BigInteger.ZERO, BigInteger::add);
        Iterator<SecurityTrade> tradeIterator = trades.iterator();
        return SecurityAggregatedTrade.builder()
                .identifier(key)
                .total(total)
                .currency(tradeIterator.hasNext() ? tradeIterator.next().getSecurity().getExchange().getCurrency() : null)
                .quantity(quantity)
                .build();
    }

    private BigDecimal calculateTradeTotal(SecurityTrade trade) {
        BigDecimal total = trade.getPrice().multiply(new BigDecimal(trade.getQuantity()));
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return total.negate();
        }
        return total;
    }

    private BigInteger calculateTotalQuantity(SecurityTrade trade) {
        BigInteger quantity = trade.getQuantity();
        if (TradeOperation.SELL.equals(trade.getOperation())) {
            return quantity.negate();
        }
        return quantity;
    }

    @Override
    @Transactional
    public TradeDto addTrade(AddTradeDto dto, String username, Security tradedSecurity) {
        User user = userService.findByUsername(username);
        var trade = SecurityTrade.builder()
                .security(tradedSecurity)
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
}
