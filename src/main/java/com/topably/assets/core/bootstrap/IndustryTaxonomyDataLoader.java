package com.topably.assets.core.bootstrap;

import com.topably.assets.companies.domain.IndustryGroup;
import com.topably.assets.companies.domain.Sector;
import com.topably.assets.companies.domain.dto.IndustryTaxonomyDto;
import com.topably.assets.companies.repository.IndustryGroupRepository;
import com.topably.assets.companies.repository.SectorRepository;
import com.topably.assets.companies.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Order(3)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
/*
  See https://en.wikipedia.org/wiki/Global_Industry_Classification_Standard
 */
public class IndustryTaxonomyDataLoader implements CommandLineRunner {

    private final IndustryGroupRepository industryGroupRepository;
    private final SectorRepository sectorRepository;

    private final IndustryService industryService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addEnergy();
        addMaterials();
    }

    private void addEnergy() {
        Sector energy = sectorRepository.save(Sector.builder()
                .name("Energy")
                .build());
        IndustryGroup energyIndustryGroup = industryGroupRepository.save(IndustryGroup.builder()
                .name("Energy")
                .sector(energy)
                .build());
        IndustryTaxonomyDto dto = IndustryTaxonomyDto.builder()
                .sectorName(energy.getName())
                .industryGroupName(energyIndustryGroup.getName())
                .industryName("Oil, Gas & Consumable Fuels")
                .subIndustryName("Integrated Oil & Gas")
                .build();
        industryService.addIndustry(dto);
    }

    private void addMaterials() {
        Sector materials = sectorRepository.save(Sector.builder()
                .name("Materials")
                .build());
        industryGroupRepository.save(IndustryGroup.builder()
                .name("Materials")
                .sector(materials)
                .build());
    }
}
