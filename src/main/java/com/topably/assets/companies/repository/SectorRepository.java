package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findByName(String name);
}
