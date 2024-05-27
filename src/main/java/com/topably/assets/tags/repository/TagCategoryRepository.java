package com.topably.assets.tags.repository;

import com.topably.assets.tags.domain.TagCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<TagCategory> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"tags"})
    Optional<TagCategory> findTagCategoryByUserIdAndCode(Long userId, String code);
}
