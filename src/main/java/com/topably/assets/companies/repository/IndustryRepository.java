package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Industry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry, Long> {

    @EntityGraph(attributePaths = {"sector"})
    Optional<Industry> findBySector_IdAndName(Long sectorId, String industryName);

    @EntityGraph(attributePaths = {"sector"})
    Optional<Industry> findBySector_NameAndName(String sectorName, String industryName);

}
