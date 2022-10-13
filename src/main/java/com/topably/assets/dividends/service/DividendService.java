package com.topably.assets.dividends.service;

import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.domain.dto.DividendData;
import com.topably.assets.core.domain.Ticker;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collection;

public interface DividendService {

    Collection<Dividend> findDividends(String ticker, String exchange);

    void addDividends(String ticker, String exchange, Collection<DividendData> dividendData);

    BigDecimal calculateAnnualDividend(Ticker ticker, Year year);

    BigDecimal calculateProbableAnnualDividend(Ticker ticker, Year year);
}
