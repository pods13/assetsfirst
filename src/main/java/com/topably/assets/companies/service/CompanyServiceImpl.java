package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.PatchCompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;

    @Override
    @Transactional
    public CompanyDto patchCompany(Long companyId, PatchCompanyDto dto) {
        var company = companyRepository.findById(companyId).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        Optional<Industry> subIndustry = industryRepository.findById(dto.getSubIndustryId());
        company.setSubIndustry(subIndustry.orElse(company.getSubIndustry()));
        Company updateCompany = companyRepository.save(company);
        return CompanyDto.builder()
                .id(updateCompany.getId())
                .name(updateCompany.getName())
                .subIndustryId(updateCompany.getSubIndustryId())
                .build();
    }
}
