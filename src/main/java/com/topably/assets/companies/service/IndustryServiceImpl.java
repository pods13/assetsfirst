package com.topably.assets.companies.service;

import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.Sector;
import com.topably.assets.companies.domain.dto.IndustryDto;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
import com.topably.assets.companies.repository.IndustryRepository;
import com.topably.assets.companies.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final SectorRepository sectorRepository;
    private final IndustryRepository industryRepository;

    @Override
    public Collection<IndustryDto> findAll() {
        return industryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public IndustryDto addIndustry(IndustryTaxonomyDto dto) {
        if (dto.getSectorName() == null || dto.getIndustryName() == null) {
            return null;
        }
        var sector = sectorRepository.findByName(dto.getSectorName())
                .orElseGet(() -> sectorRepository.save(Sector.builder().name(dto.getSectorName()).build()));
        Industry industry = industryRepository.findBySector_IdAndName(sector.getId(), dto.getIndustryName())
                .orElseGet(() -> industryRepository.save(Industry.builder().name(dto.getIndustryName()).sector(sector).build()));

        return convertToDto(industry);
    }

    private IndustryDto convertToDto(Industry industry) {
        return IndustryDto.builder()
                .id(industry.getId())
                .name(industry.getName())
                .sectorId(industry.getSector().getId())
                .build();
    }
}
