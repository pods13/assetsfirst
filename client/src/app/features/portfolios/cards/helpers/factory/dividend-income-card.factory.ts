import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { DividendIncomeCard, TimeFrameOption } from '../../types/in/dividend-income-card';

export class DividendIncomeCardFactory extends CardFactory<DividendIncomeCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): DividendIncomeCard {
    return {
      ...this.originCard,
      title: 'Dividend Income',
      cols: 3,
      rows: 4,
      timeFrame: TimeFrameOption.YEAR
    };
  }
}
