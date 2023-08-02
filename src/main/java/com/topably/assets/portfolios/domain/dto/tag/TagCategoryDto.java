package com.topably.assets.portfolios.domain.dto.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import java.util.List;

@Data
@Accessors(chain = true)
public class TagCategoryDto {

    private Long id;
    private String name;
    private String color;
    private List<TagDto> tags;
}
