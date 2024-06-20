package com.topably.assets.instruments.domain.dto;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.InstrumentType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ImportInstrumentDto {

    @NotNull
    private Ticker identifier;
    @NotNull
    private InstrumentType type;
    @NotBlank
    private String name;
    @Nullable
    private String sector;
    @Nullable
    private String industry;
}
