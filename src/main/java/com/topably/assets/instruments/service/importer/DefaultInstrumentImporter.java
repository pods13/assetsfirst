package com.topably.assets.instruments.service.importer;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.domain.dto.ImportedInstrumentDto;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.tags.domain.Tag;
import com.topably.assets.tags.service.TagCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.topably.assets.instruments.domain.InstrumentTag.INDUSTRY_TAG_CATEGORY;
import static com.topably.assets.instruments.domain.InstrumentTag.SECTOR_TAG_CATEGORY;


@Component
@RequiredArgsConstructor
public class DefaultInstrumentImporter {

    private final InstrumentRepository instrumentRepository;
    private final TagCategoryService tagCategoryService;

    private ImportedInstrumentDto addInstrument(ImportInstrumentDto dto) {
        var instrument = new Instrument()
            .setName(dto.getName())
            .setSymbol(dto.getIdentifier().getSymbol())
            .setExchangeCode(dto.getIdentifier().getExchange())
            .setCurrency(ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency());
        var sector = Optional.ofNullable(dto.getSector()).map(this::getSectorByName).orElse(null);
        instrument.addTag(sector);
        var industry = Optional.ofNullable(dto.getIndustry()).map(this::getIndustryByName).orElse(null);
        instrument.addTag(industry);
        var savedStock = instrumentRepository.save(instrument);

        return convertToDto(savedStock);
    }

    private Tag getSectorByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(SECTOR_TAG_CATEGORY, name).orElse(null);
    }

    private Tag getIndustryByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(INDUSTRY_TAG_CATEGORY, name).orElse(null);
    }

    @Transactional
    public ImportedInstrumentDto importInstrument(ImportInstrumentDto dto) {
        return instrumentRepository.findBySymbolAndExchangeCodeIncludeTags(dto.getIdentifier().getSymbol(),
                dto.getIdentifier().getExchange())
            .map(stock -> updateInstrument(stock, dto))
            .orElseGet(() -> addInstrument(dto));
    }

    private ImportedInstrumentDto updateInstrument(Instrument instrument, ImportInstrumentDto dto) {
        instrument.setName(dto.getName());
        instrument.setTags(enrichTags(instrument, dto.getSector(), this::getSectorByName));
        instrument.setTags(enrichTags(instrument, dto.getIndustry(), this::getIndustryByName));
        instrumentRepository.save(instrument);
        return convertToDto(instrument);
    }

    private Set<Tag> enrichTags(Instrument stock, String tagName, Function<String, Tag> getTagByName) {
        if (stock.getTags().stream()
            .anyMatch(t -> t.getName().equals(tagName))) {
            return stock.getTags();
        }
        return Optional.ofNullable(tagName).map(getTagByName)
            .map(tag -> {
                var updatedTags = stock.getTags()
                    .stream()
                    .filter(t -> !t.getCategory().getId().equals(tag.getCategory().getId()))
                    .collect(Collectors.toSet());
                updatedTags.add(tag);
                return updatedTags;
            }).orElse(stock.getTags());
    }

    private ImportedInstrumentDto convertToDto(Instrument instrument) {
        return new ImportedInstrumentDto()
            .setId(instrument.getId())
            .setIdentifier(instrument.toTicker());
    }

}
