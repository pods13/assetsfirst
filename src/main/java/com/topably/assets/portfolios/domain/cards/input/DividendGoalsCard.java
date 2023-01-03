package com.topably.assets.portfolios.domain.cards.input;

import com.topably.assets.portfolios.domain.cards.DashboardCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DividendGoalsCard extends DashboardCard {

    private Map<String, BigDecimal> desiredYieldByIssuer = new HashMap<>();

}
