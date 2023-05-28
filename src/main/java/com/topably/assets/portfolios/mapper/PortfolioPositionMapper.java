package com.topably.assets.portfolios.mapper;

import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PortfolioPositionMapper {

    @Mapping(target = "instrumentId", source = "position.instrument.id")
    @Mapping(target = "instrumentType", source = "position.instrument.instrumentType")
    @Mapping(target = "identifier", expression = "java(position.getInstrument().toTicker())")
    @Mapping(target = "currency", source = "position.instrument.currency")
    @Mapping(target = "price", source = "position.averagePrice")
    PortfolioPositionDto modelToDto(PortfolioPosition position);
}
