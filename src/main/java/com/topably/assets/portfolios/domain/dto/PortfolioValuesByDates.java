package com.topably.assets.portfolios.domain.dto;


import java.math.BigDecimal;
import java.util.List;

public record PortfolioValuesByDates(List<String> dates, List<BigDecimal> values) {

}
