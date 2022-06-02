package com.topably.assets.dividends.service;

import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.domain.dto.DividendData;
import com.topably.assets.core.domain.TickerSymbol;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Collection;

public interface DividendService {

    Collection<Dividend> findDividends(String ticker, String exchange);

    void addDividends(String ticker, String exchange, Collection<DividendData> dividendData);

    BigDecimal calculateAnnualDividend(TickerSymbol tickerSymbol, Year year);
}
