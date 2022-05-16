package com.topably.assets.instruments.domain.dto;

import com.topably.assets.instruments.domain.InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrumentDto {

    private Long id;

    private String ticker;

    private String name;

    private InstrumentType instrumentType;
}
