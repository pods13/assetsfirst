package com.topably.assets.portfolios.domain.cards.input.allocation;

import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CustomSegment {

    private String name;
    private List<TagWithCategoryDto> tags;
}
