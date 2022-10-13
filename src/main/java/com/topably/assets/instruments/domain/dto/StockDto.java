package com.topably.assets.instruments.domain.dto;

import com.topably.assets.core.domain.Ticker;
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

    private Ticker identifier;

    private Long companyId;

}
