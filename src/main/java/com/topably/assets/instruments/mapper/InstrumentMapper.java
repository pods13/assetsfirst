package com.topably.assets.instruments.mapper;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.domain.instrument.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassMapping;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InstrumentMapper {

    @SubclassMapping(target = InstrumentDto.class, source = Stock.class)
    @SubclassMapping(target = InstrumentDto.class, source = ETF.class)
    @SubclassMapping(target = InstrumentDto.class, source = FX.class)
    InstrumentDto modelToDto(Instrument instrument);

    @Mapping(target = "currencyCode", source = "currency.currencyCode")
    InstrumentDto stockToDto(Stock stock);

    @Mapping(target = "currencyCode", source = "currency.currencyCode")
    InstrumentDto etfToDto(ETF etf);

    @Mapping(target = "name", expression = "java(\"Currency\")")
    @Mapping(target = "currencyCode", source = "currency.currencyCode")
    InstrumentDto fxToDto(FX fx);

    default Instrument importDtoToModel(ImportInstrumentDto dto) {
        if (InstrumentType.STOCK.equals(dto.getType())) {
            return importDtoToStock(dto);
        } else if (InstrumentType.ETF.equals(dto.getType())) {
            return importDtoToEtf(dto);
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + dto.getType());
        }
    };

    @Mapping(target = "symbol", source = "identifier.symbol")
    @Mapping(target = "exchangeCode", source = "identifier.exchange")
    @Mapping(target = "attributes", expression = "java(java.util.Collections.emptyMap())")
    @Mapping(target = "currency", expression = "java(com.topably.assets.findata.exchanges.domain.ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency())")
    Stock importDtoToStock(ImportInstrumentDto dto);

    @Mapping(target = "symbol", source = "identifier.symbol")
    @Mapping(target = "exchangeCode", source = "identifier.exchange")
    @Mapping(target = "attributes", expression = "java(java.util.Collections.emptyMap())")
    @Mapping(target = "currency", expression = "java(com.topably.assets.findata.exchanges.domain.ExchangeEnum.valueOf(dto.getIdentifier().getExchange()).getCurrency())")
    ETF importDtoToEtf(ImportInstrumentDto dto);

}
