package com.topably.assets.instruments.domain.dto;

import com.topably.assets.core.domain.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDataDto {

    @NotNull
    private Ticker identifier;

    @NotNull
    private CompanyDataDto company;
}
