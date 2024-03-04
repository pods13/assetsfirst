package com.topably.assets.trades.domain.dto.manage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditTradeDto {

    private Long tradeId;
    private Long instrumentId;
    private LocalDate date;
    private BigDecimal price;
    private BigInteger quantity;

    private Long userId;
    private Long intermediaryId;
}
