package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.dto.AddCompanyDto;
import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
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

    private final IndustryService industryService;

    @Override
    public Optional<CompanyDto> findCompanyByName(String name) {
        return companyRepository.findByName(name).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CompanyDto addCompany(AddCompanyDto dto) {
        var taxonomyDto = IndustryTaxonomyDto.builder()
                .sectorName(dto.getSector()).industryName(dto.getIndustry()).build();
        var industryDto = industryService.addIndustry(taxonomyDto);
        var industry = Optional.ofNullable(industryDto).map(i -> industryRepository.getById(i.getId())).orElse(null);
        Company company = companyRepository.save(Company.builder().name(dto.getName())
                .industry(industry)
                .build());
        return convertToDto(company);
    }

    @Override
    @Transactional
    public CompanyDto patchCompany(Long companyId, PatchCompanyDto dto) {
        var company = companyRepository.findById(companyId).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        Optional<Industry> subIndustry = industryRepository.findById(dto.getIndustryId());
        company.setIndustry(subIndustry.orElse(company.getIndustry()));
        Company updateCompany = companyRepository.save(company);
        return convertToDto(updateCompany);
    }

    private CompanyDto convertToDto(Company company) {
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .industryId(Optional.ofNullable(company.getIndustry()).map(Industry::getId).orElse(null))
                .build();
    }
}
