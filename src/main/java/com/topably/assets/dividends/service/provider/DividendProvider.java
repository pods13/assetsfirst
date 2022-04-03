package com.topably.assets.dividends.service.provider;

import com.topably.assets.dividends.domain.dto.DividendData;

import java.util.Collection;

public interface DividendProvider {

    Collection<DividendData> getDividendHistory(String ticker);
}
