package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.IndustryGroup;
import com.topably.assets.companies.domain.Sector;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryGroupRepository extends JpaRepository<IndustryGroup, Long> {

    @EntityGraph(attributePaths = {"sector"})
    IndustryGroup findBySector_NameAndName(String sectorName, String name);
}
