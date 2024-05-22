package com.topably.assets.instruments.service;

import java.util.Collection;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.service.CompanyService;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;

    private final CompanyService companyService;

    @Override
    public Page<StockDto> findAll(Pageable pageable) {
        return stockRepository.findAll(pageable)
            .map(this::convertToDto);
    }

    @Override
    public Collection<Stock> findAllById(Collection<Long> ids) {
        return stockRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public StockDto addStock(StockDataDto dto) {
        CompanyDto companyDto = companyService.findCompanyByName(dto.getCompany().getName()).orElseGet(() -> {
            return companyService.addCompany(dto.getCompany());
        });
        Stock stock = stockRepository.save(Stock.builder()
            .company(companyRepository.getReferenceById(companyDto.getId()))
            .name(dto.getCompany().getName())
            .symbol(dto.getIdentifier().getSymbol())
            .exchangeCode(dto.getIdentifier().getExchange())
            .currency(ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency())
            .build());
        return convertToDto(stock);
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
        return convertToDto(stock);
    }

    private StockDto convertToDto(Stock stock) {
        return StockDto.builder()
            .id(stock.getId())
            .identifier(stock.toTicker())
            .companyId(stock.getCompany().getId())
            .build();
    }

}
