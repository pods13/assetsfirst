package com.topably.assets.portfolios.domain.dto.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class CreateTagCategoryDto {

    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private List<TagDto> tags;
}
