package com.topably.assets.dividends.service;

import com.topably.assets.dividends.domain.dto.DividendData;

import java.util.Collection;

public interface DividendService {

    void addDividends(String ticker, String exchange, Collection<DividendData> dividendData);
}
