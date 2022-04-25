package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    Sector findByName(String name);
}
