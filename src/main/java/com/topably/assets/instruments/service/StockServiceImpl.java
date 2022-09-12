package com.topably.assets.instruments.service;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.service.CompanyService;
import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;
    private final ExchangeRepository exchangeRepository;

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
                .company(companyRepository.getById(companyDto.getId()))
                .ticker(dto.getIdentifier().getSymbol())
                .exchange(exchangeRepository.findByCode(dto.getIdentifier().getExchange()))
                .build());
        return convertToDto(stock);
    }

    @Override
    @Transactional
    public StockDto importStock(StockDataDto dto) {
        return stockRepository.findByTickerAndExchange_Code(dto.getIdentifier().getSymbol(), dto.getIdentifier().getExchange())
                .map(stock -> updateStock(stock, dto))
                .orElseGet(() -> addStock(dto));
    }

    private StockDto updateStock(Stock stock, StockDataDto dto) {
        companyService.updateCompany(stock.getCompany().getId(), dto.getCompany());
        return convertToDto(stock);
    }

    private StockDto convertToDto(Stock stock) {
        return StockDto.builder()
                .id(stock.getId())
                .identifier(new TickerSymbol(stock.getTicker(), stock.getExchange().getCode()))
                .companyId(stock.getCompany().getId())
                .build();
    }
}
