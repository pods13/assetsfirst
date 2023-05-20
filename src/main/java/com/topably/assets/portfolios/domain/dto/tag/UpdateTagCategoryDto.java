package com.topably.assets.portfolios.domain.dto.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateTagCategoryDto {

    @NotBlank
    private String name;
    @NotNull
    private List<TagDto> tags;
}
