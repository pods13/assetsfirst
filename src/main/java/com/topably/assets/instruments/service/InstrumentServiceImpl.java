package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public Collection<InstrumentDto> searchTradingInstruments(Specification<Instrument> specification) {
        Collection<Instrument> instruments = instrumentRepository.findAll(specification);
        return instruments.stream().map(instrument -> {
            if (instrument instanceof Stock) {
                return InstrumentDto.builder()
                        .id(instrument.getId())
                        .ticker(instrument.getTicker())
                        .name(((Stock) instrument).getCompany().getName())
                        .instrumentType(InstrumentType.STOCK)
                        .build();
            } else if (instrument instanceof ETF) {
                return InstrumentDto.builder()
                        .id(instrument.getId())
                        .ticker(instrument.getTicker())
                        .name(((ETF) instrument).getName())
                        .instrumentType(InstrumentType.ETF)
                        .build();
            } else if (instrument instanceof FX) {
                return InstrumentDto.builder()
                        .id(instrument.getId())
                        .ticker(instrument.getTicker())
                        .instrumentType(InstrumentType.FX)
                        .build();
            }
            return InstrumentDto.builder()
                    .id(instrument.getId())
                    .ticker(instrument.getTicker())
                    .build();
        }).collect(toList());
    }

    @Override
    public Instrument findInstrument(String ticker, String exchange) {
        return instrumentRepository.findByTickerAndExchange_Code(ticker, exchange);
    }
}
