package com.topably.assets.instruments.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.dto.AddCompanyDto;
import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.service.CompanyService;
import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.dto.AddStockDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

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
    public StockDto addStock(AddStockDto dto) {
        CompanyDto companyDto = companyService.findCompanyByName(dto.getCompanyName()).orElseGet(() -> {
            AddCompanyDto addCompanyDto = AddCompanyDto.builder().name(dto.getCompanyName())
                    .sector(dto.getSectorName()).industry(dto.getIndustryName()).build();
            return companyService.addCompany(addCompanyDto);
        });
        Stock stock = stockRepository.save(Stock.builder()
                .company(companyRepository.getById(companyDto.getId()))
                .ticker(dto.getIdentifier().getSymbol())
                .exchange(exchangeRepository.findByCode(dto.getIdentifier().getExchange()))
                .build());
        return convertToDto(stock);
    }

    private StockDto convertToDto(Stock stock) {
        return StockDto.builder()
                .id(stock.getId())
                .identifier(new TickerSymbol(stock.getTicker(), stock.getExchange().getCode()))
                .companyId(Optional.ofNullable(stock.getCompany()).map(Company::getId).orElse(null))
                .build();
    }
}
