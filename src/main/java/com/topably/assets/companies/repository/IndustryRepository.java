package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry, Long> {

    Optional<Industry> findBySector_IdAndName(Long sectorId, String industryName);
}
