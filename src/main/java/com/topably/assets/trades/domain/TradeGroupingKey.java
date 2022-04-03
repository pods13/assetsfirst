package com.topably.assets.trades.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeGroupingKey implements Serializable {

    private String ticker;
    private String tradeCategory;
    private String username;
}
