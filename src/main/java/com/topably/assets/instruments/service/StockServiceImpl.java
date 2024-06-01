package com.topably.assets.instruments.service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.service.CompanyService;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
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

    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;

    private final CompanyService companyService;
    private final TagCategoryService tagCategoryService;

    @Override
    public Page<StockDto> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
            .map(this::convertToDto);
    }

    @Override
    @Transactional
    public StockDto addStock(StockDataDto dto) {
        CompanyDto companyDto = companyService.findCompanyByName(dto.getCompany().getName()).orElseGet(() -> {
            return companyService.addCompany(dto.getCompany());
        });
        var stock = Stock.builder()
            .company(companyRepository.getReferenceById(companyDto.getId()))
            .name(dto.getCompany().getName())
            .symbol(dto.getIdentifier().getSymbol())
            .exchangeCode(dto.getIdentifier().getExchange())
            .currency(ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency())
            .build();
        var sector = Optional.ofNullable(dto.getCompany().getSector()).map(this::getSectorByName).orElse(null);
        stock.addTag(sector);
        var industry = Optional.ofNullable(dto.getCompany().getIndustry()).map(this::getIndustryByName).orElse(null);
        stock.addTag(industry);
        var savedStock = stockRepository.save(stock);

        return convertToDto(savedStock);
    }

    private Tag getSectorByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(SECTOR_TAG_CATEGORY, name);
    }

    private Tag getIndustryByName(String name) {
        return tagCategoryService.findTagByCategoryCodeAndName(INDUSTRY_TAG_CATEGORY, name);
    }

    @Override
    @Transactional
    public StockDto importStock(StockDataDto dto) {
        return stockRepository.findBySymbolAndExchangeCode(dto.getIdentifier().getSymbol(), dto.getIdentifier().getExchange())
            .map(stock -> updateStock(stock, dto))
            .orElseGet(() -> addStock(dto));
    }

    private StockDto updateStock(Stock stock, StockDataDto dto) {
        companyService.updateCompany(stock.getCompany().getId(), dto.getCompany());
        stock.setName(dto.getCompany().getName());
        stock.setTags(enrichTags(stock, dto.getCompany().getSector(), this::getSectorByName));
        stock.setTags(enrichTags(stock, dto.getCompany().getIndustry(), this::getIndustryByName));
        stockRepository.save(stock);
        return convertToDto(stock);
    }

    private Set<Tag> enrichTags(Stock stock, String tagName, Function<String, Tag> getTagByName) {
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

    private StockDto convertToDto(Stock stock) {
        return StockDto.builder()
            .id(stock.getId())
            .identifier(stock.toTicker())
            .companyId(stock.getCompany().getId())
            .build();
    }

}
