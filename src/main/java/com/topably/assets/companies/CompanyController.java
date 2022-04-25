package com.topably.assets.companies;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.PatchCompanyDto;
import com.topably.assets.companies.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PatchMapping("/{companyId}")
    public CompanyDto patchCompany(@PathVariable Long companyId, PatchCompanyDto dto) {
        return companyService.patchCompany(companyId, dto);
    }
}
