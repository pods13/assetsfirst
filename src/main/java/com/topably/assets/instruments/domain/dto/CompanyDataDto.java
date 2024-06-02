package com.topably.assets.instruments.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDataDto {

    @NotBlank
    private String name;
    @Nullable
    private String sector;
    @Nullable
    private String industry;

}
