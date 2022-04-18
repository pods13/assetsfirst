package com.topably.assets.trades.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySymbol implements Serializable {

    private String symbol;
    private String exchange;
}
