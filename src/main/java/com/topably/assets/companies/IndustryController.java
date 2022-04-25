package com.topably.assets.companies;

import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
import com.topably.assets.companies.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/industries")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryService industryService;

    @PostMapping
    public IndustryDto addIndustryByTaxonomy(@Validated @RequestBody IndustryTaxonomyDto dto) {
        return industryService.addIndustry(dto);
    }
}
