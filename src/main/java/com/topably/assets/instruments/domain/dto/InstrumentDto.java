package com.topably.assets.instruments.domain.dto;

import com.topably.assets.instruments.domain.InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class InstrumentDto {

    private Long id;
    private String symbol;
    private String name;
    private InstrumentType instrumentType;
    private String currencyCode;
}
