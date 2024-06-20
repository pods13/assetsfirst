package com.topably.assets.instruments.domain.dto;

import com.topably.assets.core.domain.Ticker;
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
public class ImportedInstrumentDto {

    private Long id;

    private Ticker identifier;
}
