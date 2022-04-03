package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
