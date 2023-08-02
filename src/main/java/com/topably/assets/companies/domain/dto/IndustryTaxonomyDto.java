package com.topably.assets.companies.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndustryTaxonomyDto {

    @NotBlank
    private String sectorName;
    @NotBlank
    private String industryName;
}
