package com.topably.assets.portfolios.mapper;

import com.topably.assets.portfolios.domain.dto.tag.SelectedTagDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.tag.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagMapper {

    TagDto modelToDto(Tag tag);

    SelectedTagDto modelToSelectedDto(Tag tag);
}
