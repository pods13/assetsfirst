package com.topably.assets.portfolios.domain.cards;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.input.ContributionCard;
import com.topably.assets.portfolios.domain.cards.input.allocation.AllocationCard;
import com.topably.assets.portfolios.domain.cards.input.DividendGoalsCard;
import com.topably.assets.portfolios.domain.cards.input.DividendIncomeCard;
import com.topably.assets.portfolios.domain.cards.input.SectoralDistributionCard;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "containerType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = CardContainerType.ALLOCATION, value = AllocationCard.class),
        @JsonSubTypes.Type(name = CardContainerType.DIVIDEND_INCOME, value = DividendIncomeCard.class),
        @JsonSubTypes.Type(name = CardContainerType.DIVIDEND_GOALS, value = DividendGoalsCard.class),
        @JsonSubTypes.Type(name = CardContainerType.SECTORAL_DISTRIBUTION, value = SectoralDistributionCard.class),
        @JsonSubTypes.Type(name = CardContainerType.BALANCE, value = BalanceCard.class),
        @JsonSubTypes.Type(name = CardContainerType.CONTRIBUTION, value = ContributionCard.class),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@NoArgsConstructor
public abstract class DashboardCard implements Serializable {

    private String id;
    private String containerType;
    private String title;

    private Integer x;
    private Integer y;
    private Integer rows;
    private Integer cols;
    private Integer minItemRows;
    private Integer minItemCols;
}
