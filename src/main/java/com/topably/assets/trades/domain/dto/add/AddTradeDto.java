package com.topably.assets.trades.domain.dto.add;

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
public class AddTradeDto {

    private Long instrumentId;
    private TradeOperation operation;
    private LocalDateTime date;
    private BigDecimal price;
    private BigInteger quantity;

    private String username;
    private Long brokerId;
}
