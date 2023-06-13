package com.topably.assets.companies.util;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.Stock;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CompanyUtils {

    public String resolveCompanyName(Instrument instrument) {
        var instrumentType = instrument.getInstrumentType();
        if (InstrumentType.STOCK.name().equals(instrumentType)) {
            var stock = (Stock) instrument;
            return stock.getCompany().getName();
        } else if (InstrumentType.FX.name().equals(instrumentType)) {
            return "Currency";
        } else if (InstrumentType.ETF.name().equals(instrumentType)) {
            var etf = (ETF) instrument;
            return etf.getName();
        }
        throw new UnsupportedOperationException("Such instrument type %s is not yet supported".formatted(instrumentType));
    }
}
