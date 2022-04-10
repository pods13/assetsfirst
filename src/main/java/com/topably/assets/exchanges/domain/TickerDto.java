package com.topably.assets.exchanges.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TickerDto {

    //TODO rename code field to symbol
    private String code;
    private String exchange;
}
