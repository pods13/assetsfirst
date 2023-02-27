package com.topably.assets.portfolios.domain.dto.tag;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TagDto {

    private Long id;
    private String name;
}
