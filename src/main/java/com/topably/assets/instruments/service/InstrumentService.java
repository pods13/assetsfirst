package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public interface InstrumentService {

    Collection<InstrumentDto> searchTradingInstruments(Specification<Instrument> specification);

    Instrument findInstrument(String ticker, String exchange);
}
