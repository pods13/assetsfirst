package com.topably.assets.instruments.service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
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
public class StockServiceImpl implements StockService {

    private final InstrumentRepository instrumentRepository;
    private final StockRepository stockRepository;

    private final TagCategoryService tagCategoryService;

    @Override
    public Page<StockDto> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional
    public StockDto addStock(StockDataDto dto) {
        var stock = new Instrument()
            .setName(dto.getCompany().getName())
            .setSymbol(dto.getIdentifier().getSymbol())
            .setExchangeCode(dto.getIdentifier().getExchange())
            .setCurrency(ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency());
        var sector = Optional.ofNullable(dto.getCompany().getSector()).map(this::getSectorByName).orElse(null);
        stock.addTag(sector);
        var industry = Optional.ofNullable(dto.getCompany().getIndustry()).map(this::getIndustryByName).orElse(null);
        stock.addTag(industry);
        var savedStock = instrumentRepository.save(stock);

        return convertToDto(savedStock);
    }

    private Tag getSectorByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(SECTOR_TAG_CATEGORY, name).orElse(null);
    }

    private Tag getIndustryByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(INDUSTRY_TAG_CATEGORY, name).orElse(null);
    }

    @Override
    @Transactional
    public StockDto importStock(StockDataDto dto) {
        return instrumentRepository.findBySymbolAndExchangeCodeIncludeTags(dto.getIdentifier().getSymbol(), dto.getIdentifier().getExchange())
            .map(stock -> updateStock(stock, dto))
            .orElseGet(() -> addStock(dto));
    }

    private StockDto updateStock(Instrument stock, StockDataDto dto) {
        stock.setName(dto.getCompany().getName());
        stock.setTags(enrichTags(stock, dto.getCompany().getSector(), this::getSectorByName));
        stock.setTags(enrichTags(stock, dto.getCompany().getIndustry(), this::getIndustryByName));
        instrumentRepository.save(stock);
        return convertToDto(stock);
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

    private StockDto convertToDto(Instrument stock) {
        return new StockDto()
            .setId(stock.getId())
            .setIdentifier(stock.toTicker());
    }

}
