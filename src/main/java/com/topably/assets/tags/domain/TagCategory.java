package com.topably.assets.tags.domain;

import com.topably.assets.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "user_tag_category",
            joinColumns = {@JoinColumn(name = "tag_category_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private User user;


    private String name;

    private String code;

    @Column(columnDefinition = "char(7)")
    private String color;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tag> tags;
}
