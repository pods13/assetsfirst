package com.topably.assets.findata.dividends.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public record PubDividendDto(LocalDate declareDate, LocalDate recordDate, LocalDate payDate, BigDecimal amount, String currency) {
}
