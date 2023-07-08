package com.topably.assets.portfolios.mapper;

import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.portfolios.domain.dto.PortfolioDividendDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PortfolioDividendMapper {

    @Mapping(target = "perShare", source = "dividend.amount")
    @Mapping(target = "currency", source = "dividend.instrument.currency")
    PortfolioDividendDto modelToDto(Dividend dividend, BigInteger quantity);

    @AfterMapping
    default void modelToDtoAfterMapping(Dividend dividend, BigInteger quantity, @MappingTarget PortfolioDividendDto dto) {
        var instrument = dividend.getInstrument();
        if (instrument instanceof Stock stock) {
            dto.setName(stock.getCompany().getName());
        } else if (instrument instanceof ETF etf) {
            dto.setName(etf.getName());
        }
        dto.setTotal(dividend.getAmount().multiply(new BigDecimal(quantity)));
    }
}
