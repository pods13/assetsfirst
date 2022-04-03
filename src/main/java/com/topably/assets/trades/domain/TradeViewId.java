package com.topably.assets.trades.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeViewId implements Serializable {

    private Long id;
    private String tradeCategory;
    private String username;
}
