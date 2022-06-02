package com.topably.assets.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TickerSymbol {

    private String symbol;
    private String exchange;

    @Override
    public String toString() {
        return symbol + '.' + exchange;
    }
}
