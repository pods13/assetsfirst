package com.topably.assets.instruments.domain.dto;

import com.topably.assets.core.domain.TickerSymbol;
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
public class AddStockDto {

    private String companyName;

    private TickerSymbol identifier;

    private String sectorName;

    private String industryName;

}
