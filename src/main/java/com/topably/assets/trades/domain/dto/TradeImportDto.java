package com.topably.assets.trades.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TradeImportDto {

    private String companyName;
    private String isin;
    private LocalDateTime date;
    private String operation;
    private BigInteger quantity;
    private String currency;
    private BigDecimal price;
    private BigDecimal fee;
    private String tradeNum;
}
