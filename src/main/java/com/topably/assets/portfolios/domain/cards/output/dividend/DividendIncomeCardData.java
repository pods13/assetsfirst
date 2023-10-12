package com.topably.assets.portfolios.domain.cards.output.dividend;

import com.topably.assets.portfolios.domain.cards.CardData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class DividendIncomeCardData implements CardData {

    private List<TimeFrameDividend> dividends;

}
