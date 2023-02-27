package com.topably.assets.portfolios.mapper;

import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.tag.TagCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagCategoryMapper {

    TagCategoryDto modelToDto(TagCategory tagCategory, List<TagDto> tags);
}
