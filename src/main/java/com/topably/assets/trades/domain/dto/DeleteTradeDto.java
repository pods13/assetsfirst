package com.topably.assets.trades.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTradeDto {

    private Long tradeId;
    private Long instrumentId;
}
