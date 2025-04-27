package com.topably.assets.tags.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk__tag__category_id__tag_category"))
    private TagCategory category;
}
