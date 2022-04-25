package com.topably.assets.securities.domain.dto;

import com.topably.assets.exchanges.domain.TickerSymbol;
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
public class StockDto {

    private Long id;

    private TickerSymbol identifier;

    private Long companyId;

}
