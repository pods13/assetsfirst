package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.PatchCompanyDto;

public interface CompanyService {

    CompanyDto patchCompany(Long companyId, PatchCompanyDto dto);
}
