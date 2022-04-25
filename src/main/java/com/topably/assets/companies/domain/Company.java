package com.topably.assets.companies.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
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
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String name;

    @Column(name = "SUB_INDUSTRY_ID", insertable = false, updatable = false)
    private Long subIndustryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_INDUSTRY_ID", referencedColumnName = "ID", foreignKey=@ForeignKey(name = "fk__company__sub_industry_id__industry"))
    private Industry subIndustry;
}
