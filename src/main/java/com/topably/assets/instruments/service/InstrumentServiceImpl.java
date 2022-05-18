package com.topably.assets.instruments.service;

import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class InstrumentServiceImpl implements InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public Collection<InstrumentDto> searchTradingInstruments(String searchTerm, Collection<InstrumentType> instrumentTypes) {
        var types = instrumentTypes.stream().map(InstrumentType::name).collect(toSet());
        Collection<Instrument> instruments = instrumentRepository.findAllByTickerLikeAndInstrumentTypeIn("%" + searchTerm + "%", types);
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
    public Collection<Instrument> findCertainTypeOfInstrumentsByExchangeCodes(Collection<InstrumentType> instrumentTypes,
                                                                              Collection<String> exchangeCodes) {
        Set<String> types = instrumentTypes.stream().map(InstrumentType::name).collect(toSet());
        return instrumentRepository.findAllByInstrumentTypeInAndExchange_CodeIn(types, exchangeCodes);
    }

    @Override
    public Instrument findInstrument(String ticker, String exchange) {
        return instrumentRepository.findByTickerAndExchange_Code(ticker, exchange);
    }
}
