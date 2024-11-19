package com.topably.assets.instruments.service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.ImportedInstrumentDto;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import com.topably.assets.tags.domain.Tag;
import com.topably.assets.tags.service.TagCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.topably.assets.instruments.domain.InstrumentTag.INDUSTRY_TAG_CATEGORY;
import static com.topably.assets.instruments.domain.InstrumentTag.SECTOR_TAG_CATEGORY;


@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public Page<ImportedInstrumentDto> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    private ImportedInstrumentDto convertToDto(Instrument stock) {
        return new ImportedInstrumentDto()
                .setId(stock.getId())
                .setIdentifier(stock.toTicker());
    }

}
