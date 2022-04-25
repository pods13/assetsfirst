package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.PatchCompanyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;


    @Override
    @Transactional
    public CompanyDto patchCompany(Long companyId, PatchCompanyDto dto) {
        var company = companyRepository.findById(companyId).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        company.setSubIndustryId(Optional.ofNullable(dto.getSubIndustryId()).orElse(company.getSubIndustryId()));
        return CompanyDto.builder()
                .id(company.getId())
                .subIndustryId(company.getSubIndustryId())
                .build();
    }
}
