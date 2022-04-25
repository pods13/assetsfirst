package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.IndustryGroup;
import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
import com.topably.assets.companies.repository.IndustryGroupRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final IndustryGroupRepository industryGroupRepository;
    private final IndustryRepository industryRepository;

    @Override
    @Transactional
    public IndustryDto addIndustry(IndustryTaxonomyDto dto) {
        IndustryGroup group = industryGroupRepository.findBySector_NameAndName(dto.getSectorName(), dto.getIndustryGroupName());
        if (group == null) {
            throw new RuntimeException();
        }
        var industryNames = Set.of(dto.getIndustryName(), dto.getSubIndustryName());
        List<Industry> industries = industryRepository.findAllByGroup(group).stream()
                .filter(industry -> industryNames.contains(industry.getName()))
                .collect(toList());
        if (industries.size() == 2) {
            var cause = dto.getIndustryName() + "->" + dto.getSubIndustryName();
            throw new EntityExistsException("Industries " + cause + " are already in place");
        } else if (industries.size() == 0) {
            industries.add(industryRepository.save(createIndustry(group, null, dto.getIndustryName())));
        }

        Industry subIndustry = industryRepository.save(createIndustry(group, industries.get(0), dto.getSubIndustryName()));
        return null;
    }

    private Industry createIndustry(IndustryGroup group, Industry parent, String name) {
        return Industry.builder()
                .name(name)
                .group(group)
                .parent(parent)
                .build();
    }
}
