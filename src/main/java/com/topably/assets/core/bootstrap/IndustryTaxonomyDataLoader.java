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

import java.util.Set;

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
        addIndustrials();
        addConsumerDiscretionary();
        addConsumerStaples();
        addHealthCare();
        addFinancials();
        addInformationTechnology();
        addCommunicationServices();
        addUtilities();
        addRealEstate();
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

    private void addIndustrials() {
        Sector industrials = sectorRepository.save(Sector.builder()
                .name("Industrials")
                .build());
        Set<String> groups = Set.of("Capital Goods", "Commercial & Professional Services", "Transportation");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(industrials)
                    .build());
        });
    }

    private void addConsumerDiscretionary() {
        Sector consumerDiscretionary = sectorRepository.save(Sector.builder()
                .name("Consumer Discretionary")
                .build());
        Set<String> groups = Set.of("Automobiles & Components", "Consumer Durables & Apparel", "Consumer Services", "Retailing");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(consumerDiscretionary)
                    .build());
        });
    }

    private void addConsumerStaples() {
        Sector consumerStaples = sectorRepository.save(Sector.builder()
                .name("Consumer Staples")
                .build());
        Set<String> groups = Set.of("Food & Staples Retailing", "Food, Beverage & Tobacco", "Household & Personal Products");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(consumerStaples)
                    .build());
        });
    }

    private void addHealthCare() {
        Sector healthCare = sectorRepository.save(Sector.builder()
                .name("Health Care")
                .build());
        Set<String> groups = Set.of("Health Care Equipment & Services", "Pharmaceuticals, Biotechnology & Life Sciences");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(healthCare)
                    .build());
        });
    }

    private void addFinancials() {
        Sector financials = sectorRepository.save(Sector.builder()
                .name("Financials")
                .build());
        Set<String> groups = Set.of("Banks", "Diversified Financials", "Insurance");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(financials)
                    .build());
        });
    }

    private void addInformationTechnology() {
        Sector informationTechnology = sectorRepository.save(Sector.builder()
                .name("Information Technology")
                .build());
        Set<String> groups = Set.of("Software & Services", "Technology Hardware & Equipment", "Semiconductors & Semiconductor Equipment");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(informationTechnology)
                    .build());
        });
    }

    private void addCommunicationServices() {
        Sector communicationServices = sectorRepository.save(Sector.builder()
                .name("Communication Services")
                .build());
        Set<String> groups = Set.of("Telecommunication Services", "Media & Entertainment");
        groups.forEach(group -> {
            industryGroupRepository.save(IndustryGroup.builder()
                    .name(group)
                    .sector(communicationServices)
                    .build());
        });
    }

    private void addUtilities() {
        Sector utilities = sectorRepository.save(Sector.builder()
                .name("Utilities")
                .build());
        industryGroupRepository.save(IndustryGroup.builder()
                .name("Utilities")
                .sector(utilities)
                .build());
    }

    private void addRealEstate() {
        Sector realEstate = sectorRepository.save(Sector.builder()
                .name("Real Estate")
                .build());
        industryGroupRepository.save(IndustryGroup.builder()
                .name("Real Estate")
                .sector(realEstate)
                .build());
    }
}
