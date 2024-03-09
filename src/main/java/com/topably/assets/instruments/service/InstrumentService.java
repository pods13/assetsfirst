package com.topably.assets.instruments.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.exception.WrongIdentifierException;
import com.topably.assets.instruments.mapper.InstrumentMapper;
import com.topably.assets.instruments.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class InstrumentService {

    private final InstrumentMapper instrumentMapper;
    private final InstrumentRepository instrumentRepository;

    public Collection<InstrumentDto> searchTradingInstruments(Specification<Instrument> specification) {
        var instruments = instrumentRepository.findAll(specification);
        return instruments.stream().map(instrumentMapper::modelToDto).collect(toList());
    }

    public Instrument findInstrument(String symbol, String exchange) {
        return instrumentRepository.findBySymbolAndExchangeCode(symbol, exchange);
    }

    public InstrumentDto findInstrumentByIdentifier(String identifier) {
        var ticker = transformToTicker(identifier);
        var instrument = instrumentRepository.findBySymbolAndExchangeCode(ticker.getSymbol(), ticker.getExchange());
        return instrumentMapper.modelToDto(instrument);
    }

    private Ticker transformToTicker(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            throw new WrongIdentifierException(identifier);
        }
        //identifier looks like exchange-symbol
        var exchangeBySymbol = identifier.split("-");
        if (exchangeBySymbol.length != 2) {
            throw new WrongIdentifierException(identifier);
        }
        return new Ticker(exchangeBySymbol[1].toUpperCase(), exchangeBySymbol[0].toUpperCase());
    }
}
