package com.topably.assets.exchanges.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickerSymbol {

    private String symbol;
    private String exchange;
}
