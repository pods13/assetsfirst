package com.topably.assets.trades.service.category;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.securities.domain.Stock;
import com.topably.assets.securities.repository.security.StockRepository;
import com.topably.assets.trades.domain.security.StockTrade;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddStockTradeDto;
import com.topably.assets.trades.repository.StockTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockTradeService {

    private final UserService userService;

    private final StockRepository stockRepository;
    private final StockTradeRepository stockTradeRepository;

    @Transactional
    public TradeDto addTrade(AddStockTradeDto dto, String username) {
        Stock stock = stockRepository.getById(dto.getSecurityId());
        User user = userService.findByUsername(username);
        StockTrade stockTrade = StockTrade.builder()
                .stock(stock)
                .operation(dto.getOperation())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .user(user)
                .build();
        StockTrade savedTrade = stockTradeRepository.save(stockTrade);
        return TradeDto.builder()
                .id(savedTrade.getId())
                .build();
    }
}
