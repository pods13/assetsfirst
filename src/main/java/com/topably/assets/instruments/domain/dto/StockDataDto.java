package com.topably.assets.instruments.domain.dto;

import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.TickerSymbol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDataDto {

    @NotNull
    private TickerSymbol identifier;

    @NotNull
    private CompanyDataDto company;
}
