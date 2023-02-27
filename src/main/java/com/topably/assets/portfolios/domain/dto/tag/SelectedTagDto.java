package com.topably.assets.portfolios.domain.dto.tag;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SelectedTagDto {

    private Long id;
    private Long categoryId;
    private String name;
}
