package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.companies.domain.dto.CompanyDto;
import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    public CompanyDto addCompany(CompanyDataDto dto) {
        var industryDto = addIndustry(dto);
        var industry = Optional.ofNullable(industryDto).map(i -> industryRepository.getById(i.getId())).orElse(null);
        Company company = companyRepository.save(Company.builder().name(dto.getName())
                .industry(industry)
                .build());
        return convertToDto(company);
    }

    private IndustryDto addIndustry(CompanyDataDto dto) {
        var taxonomyDto = IndustryTaxonomyDto.builder()
                .sectorName(dto.getSector()).industryName(dto.getIndustry()).build();
        return industryService.addIndustry(taxonomyDto);
    }

    @Override
    @Transactional
    public CompanyDto updateCompany(Long companyId, CompanyDataDto dto) {
        var company = companyRepository.findById(companyId).orElseThrow(() -> {
            throw new EntityNotFoundException();
        });
        company.setName(dto.getName());
        Industry industry = industryRepository.findBySector_NameAndName(dto.getSector(), dto.getIndustry())
                .orElseGet(() -> {
                    var industryDto = addIndustry(dto);
                    return industryRepository.getById(industryDto.getId());
                });
        company.setIndustry(industry);
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
