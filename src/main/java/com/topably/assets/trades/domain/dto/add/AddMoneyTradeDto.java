package com.topably.assets.trades.domain.dto.add;

import com.topably.assets.trades.domain.TradeOperation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class AddMoneyTradeDto {

    private LocalDateTime date;
    private TradeOperation operation;
    private BigDecimal amount;
    private String currencyCode;
}
