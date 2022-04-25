package com.topably.assets.companies.repository;

import com.topably.assets.companies.domain.Industry;
import com.topably.assets.companies.domain.IndustryGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface IndustryRepository extends JpaRepository<Industry, Long> {

    Industry findByParent_NameAndName(String parentName, String name);

    Collection<Industry> findAllByGroup(IndustryGroup group);
}
