package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.InstrumentDto;

import java.util.Collection;

public interface InstrumentService {

    Collection<InstrumentDto> searchTradingInstruments(String searchTerm, Collection<InstrumentType> instrumentTypes);

    Instrument findInstrument(String ticker, String exchange);
}
