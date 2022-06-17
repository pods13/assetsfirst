package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.AddCompanyDto;
import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.PatchCompanyDto;

import java.util.Optional;

public interface CompanyService {

    Optional<CompanyDto> findCompanyByName(String name);

    CompanyDto addCompany(AddCompanyDto dto);

    CompanyDto patchCompany(Long companyId, PatchCompanyDto dto);
}
