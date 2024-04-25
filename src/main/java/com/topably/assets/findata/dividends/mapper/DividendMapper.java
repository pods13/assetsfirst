package com.topably.assets.findata.dividends.mapper;

import java.util.Currency;

import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.dividends.domain.dto.PubDividendDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DividendMapper {

    @Mapping(target = "currency", source = "currency.currencyCode")
    PubDividendDto modelToDto(Dividend dividend, Currency currency);
}
