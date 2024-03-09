package com.topably.assets.findata.exchanges.domain;

import java.util.Currency;


public enum ExchangeEnum {
    FX_IDC(Currency.getInstance("USD")),
    XETRA(Currency.getInstance("EUR")),
    NYSE(Currency.getInstance("USD")),
    NYSEARCA(Currency.getInstance("USD")),
    NASDAQ(Currency.getInstance("USD")),
    MCX(Currency.getInstance("RUB")),
    HK(Currency.getInstance("HKD"));

    private final Currency currency;
    ExchangeEnum(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }
}
