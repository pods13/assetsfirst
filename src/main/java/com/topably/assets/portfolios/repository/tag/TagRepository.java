package com.topably.assets.portfolios.repository.tag;

import com.topably.assets.portfolios.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByCategoryId(Long categoryId);
}
