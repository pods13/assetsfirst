package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.companies.domain.dto.CompanyDto;

import java.util.Optional;

public interface CompanyService {

    Optional<CompanyDto> findCompanyByName(String name);

    CompanyDto addCompany(CompanyDataDto dto);

    CompanyDto updateCompany(Long companyId, CompanyDataDto dto);
}
