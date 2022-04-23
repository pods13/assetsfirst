package com.topably.assets.core.bootstrap;

import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.IndustryGroup;
import com.topably.assets.companies.domain.Sector;
import com.topably.assets.companies.repository.IndustryGroupRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import com.topably.assets.companies.repository.SectorRepository;
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

    private final IndustryRepository industryRepository;
    private final IndustryGroupRepository industryGroupRepository;
    private final SectorRepository sectorRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addEnergySector();
    }

    private void addEnergySector() {
        Sector energy = sectorRepository.save(Sector.builder()
                .name("Energy")
                .build());
        IndustryGroup energyIndustryGroup = industryGroupRepository.save(IndustryGroup.builder()
                .name("Energy")
                .build());
        Industry parentIndustry = industryRepository.save(Industry.builder()
                .name("Oil, Gas & Consumable Fuels")
                .sector(energy)
                .group(energyIndustryGroup)
                .build());

        industryRepository.save(Industry.builder()
                .name("Integrated Oil & Gas")
                .sector(energy)
                .group(energyIndustryGroup)
                .parent(parentIndustry)
                .build());
    }
}
