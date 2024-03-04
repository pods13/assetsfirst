package com.topably.assets.portfolios.repository.tag;

import com.topably.assets.portfolios.domain.tag.TagCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {

    @EntityGraph(attributePaths = {"tags"})
    List<TagCategory> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"tags"})
    List<TagCategory> findAllByUserIdAndName(Long userId, String name);
}
