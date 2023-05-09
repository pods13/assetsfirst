package com.topably.assets.portfolios.repository.tag;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.tag.TagProjection;
import com.topably.assets.portfolios.domain.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByCategoryId(Long categoryId);

    @Query(value = """
        select new com.topably.assets.portfolios.domain.dto.tag.TagProjection(t.id, t.name, c.id, c.name) from Tag t
            join t.category c
        where c.user.id = :#{#user.userId}
            and concat(lower(c.name), '::', lower(t.name)) like lower(concat('%', :searchTerm,'%'))
        """)
    Page<TagProjection> findAllTags(CurrentUser user, String searchTerm, Pageable pageable);

}
