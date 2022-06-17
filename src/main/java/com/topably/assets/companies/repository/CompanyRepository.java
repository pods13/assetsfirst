package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.dto.CompanyDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
}
