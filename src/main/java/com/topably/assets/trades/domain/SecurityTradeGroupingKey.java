package com.topably.assets.trades.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityTradeGroupingKey implements Serializable {

    private String exchange;
    private String ticker;
    private String username;
}
