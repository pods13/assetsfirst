package com.topably.assets.tags.mapper;

import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.tags.domain.TagCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagCategoryMapper {

    TagCategoryDto modelToDto(TagCategory tagCategory);
}
