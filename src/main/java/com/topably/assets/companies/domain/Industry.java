package com.topably.assets.companies.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "SECTOR_ID", referencedColumnName = "ID", foreignKey=@ForeignKey(name = "fk__industry__sector_id__sector"))
    private Sector sector;

    @ManyToOne()
    @JoinColumn(name = "INDUSTRY_GROUP_ID", referencedColumnName = "ID", foreignKey=@ForeignKey(name = "fk__industry__industry_group_id__industry_group"))
    private IndustryGroup group;

    @ManyToOne()
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID", foreignKey=@ForeignKey(name = "fk__industry__parent_id__industry"))
    private Industry parent;
}
