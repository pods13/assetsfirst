package com.topably.assets.portfolios.domain.cards.output.dividend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class TimeFrameDividend {

    private String name;
    private Collection<DividendSummary> series;
}
