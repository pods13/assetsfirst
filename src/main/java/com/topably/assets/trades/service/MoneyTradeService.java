package com.topably.assets.trades.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.trades.domain.money.MoneyTrade;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddMoneyTradeDto;
import com.topably.assets.trades.repository.MoneyTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Service
@RequiredArgsConstructor
public class MoneyTradeService {

    private final UserService userService;
    private final MoneyTradeRepository moneyTradeRepository;

    @Transactional
    public TradeDto addTrade(AddMoneyTradeDto dto, String username) {
        User user = userService.findByUsername(username);
        MoneyTrade moneyTrade = MoneyTrade.builder()
                .operation(dto.getOperation())
                .date(dto.getDate())
                .user(user)
                .amount(dto.getAmount())
                .currency(Currency.getInstance(dto.getCurrencyCode()))
                .build();
        MoneyTrade savedTrade = moneyTradeRepository.save(moneyTrade);
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
    }
}
