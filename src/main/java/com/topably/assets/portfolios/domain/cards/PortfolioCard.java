package com.topably.assets.portfolios.domain.cards;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.input.AllocationCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "containerType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AllocationCard.class, name = CardContainerType.ALLOCATION),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class PortfolioCard {

    private String id;
    private String containerType;
    private String title;

    private Integer x;
    private Integer y;
    private Integer rows;
    private Integer cols;
    private Integer minItemRows;
    private Integer minItemCols;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioCard that = (PortfolioCard) o;
        return Objects.equals(id, that.id) && Objects.equals(containerType, that.containerType) && Objects.equals(title, that.title)
                && Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(rows, that.rows) && Objects.equals(cols, that.cols)
                && Objects.equals(minItemRows, that.minItemRows) && Objects.equals(minItemCols, that.minItemCols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, containerType, title, x, y, rows, cols, minItemRows, minItemCols);
    }
}
