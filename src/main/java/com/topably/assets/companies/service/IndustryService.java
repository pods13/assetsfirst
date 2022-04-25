package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;

import java.util.Collection;

public interface IndustryService {

    Collection<IndustryDto> findAll();

    IndustryDto addIndustry(IndustryTaxonomyDto dto);
}
