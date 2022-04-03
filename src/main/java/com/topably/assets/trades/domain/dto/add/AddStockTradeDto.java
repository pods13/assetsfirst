package com.topably.assets.trades.domain.dto.add;

import com.topably.assets.trades.domain.TradeOperation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Setter
@Getter
public class AddStockTradeDto {

    private Long securityId;
    private TradeOperation operation;
    private LocalDateTime date;
    private BigDecimal price;
    private BigInteger quantity;
}
