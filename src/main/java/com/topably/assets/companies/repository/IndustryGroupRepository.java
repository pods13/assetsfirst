package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.IndustryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryGroupRepository extends JpaRepository<IndustryGroup, Long> {
}
