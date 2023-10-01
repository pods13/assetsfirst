package com.topably.assets.findata.xrates.service.provider.client;

import com.topably.assets.findata.xrates.domain.fc.CurrencyHistoricalExchangeRateData;
import com.topably.assets.findata.xrates.domain.fc.CurrencyLatestExchangeRateData;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface FreeCurrencyClient {

    @GetExchange("/latest")
    CurrencyLatestExchangeRateData getLatest(@RequestParam String apikey, @RequestParam("base_currency") String baseCurrency);

    @GetExchange("/historical")
    CurrencyHistoricalExchangeRateData getHistorical(@RequestParam String apikey,
                                                     @RequestParam("base_currency") String baseCurrency,
                                                     @RequestParam String date);
}
