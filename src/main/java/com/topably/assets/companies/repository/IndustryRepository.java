package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryRepository extends JpaRepository<Industry, Long> {

    Industry findByParent_NameAndName(String parentName, String name);
}
