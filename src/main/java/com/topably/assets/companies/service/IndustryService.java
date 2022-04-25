package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;

public interface IndustryService {

    IndustryDto addIndustry(IndustryTaxonomyDto dto);
}
