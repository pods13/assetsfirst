package com.topably.assets.trades.service;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.domain.security.SecurityTrade;
import com.topably.assets.trades.repository.SecurityTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SecurityTradeServiceImpl implements SecurityTradeService {

    private final UserService userService;
    private final SecurityTradeRepository tradeRepository;

    @Override
    @Transactional
    public Collection<SecurityTrade> getUserTrades(String username) {
        return tradeRepository.findAllByUsername(username);
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
