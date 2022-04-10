package com.topably.assets.dividends.service;

import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.domain.dto.DividendData;

import java.util.Collection;

public interface DividendService {

    Collection<Dividend> findDividends(String ticker, String exchange);

    void addDividends(String ticker, String exchange, Collection<DividendData> dividendData);
}
