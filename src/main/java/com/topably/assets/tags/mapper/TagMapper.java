package com.topably.assets.tags.mapper;

import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.dto.tag.TagProjection;
import com.topably.assets.tags.domain.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagMapper {

    TagDto modelToDto(Tag tag);

    @Mapping(source = "tag.category.id", target = "categoryId")
    @Mapping(source = "tag.category.name", target = "categoryName")
    TagProjection modelToProjection(Tag tag);
}
