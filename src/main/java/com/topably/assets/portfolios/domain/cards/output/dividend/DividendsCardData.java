package com.topably.assets.portfolios.domain.cards.output.dividend;

import com.topably.assets.portfolios.domain.cards.PortfolioCardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividendsCardData implements PortfolioCardData {

    private List<TimeFrameDividend> dividends;

}
