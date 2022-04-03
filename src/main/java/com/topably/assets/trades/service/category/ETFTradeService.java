package com.topably.assets.trades.service.category;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.securities.domain.ETF;
import com.topably.assets.securities.repository.security.ETFRepository;
import com.topably.assets.trades.domain.security.ETFTrade;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddETFTradeDto;
import com.topably.assets.trades.repository.ETFTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ETFTradeService {

    private final UserService userService;

    private final ETFRepository etfRepository;
    private final ETFTradeRepository etfTradeRepository;

    @Transactional
    public TradeDto addTrade(AddETFTradeDto dto, String username) {
        ETF etf = etfRepository.getById(dto.getSecurityId());
        User user = userService.findByUsername(username);
        ETFTrade etfTrade = ETFTrade.builder()
                .etf(etf)
                .operation(dto.getOperation())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .user(user)
                .build();
        var savedTrade = etfTradeRepository.save(etfTrade);
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
    }
}
