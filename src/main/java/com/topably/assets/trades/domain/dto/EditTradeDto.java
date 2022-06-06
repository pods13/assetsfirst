package com.topably.assets.trades.domain.dto;

import com.topably.assets.trades.domain.TradeOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditTradeDto {

    private Long tradeId;
    private LocalDateTime date;
    private BigDecimal price;
    private BigInteger quantity;

    private Long userId;
    private Long brokerId;
}
