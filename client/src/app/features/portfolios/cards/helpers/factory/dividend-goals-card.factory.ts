import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { DividendGoalsCard } from '../../types/in/dividend-goals-card';

export class DividendGoalsCardFactory extends CardFactory<DividendGoalsCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): DividendGoalsCard {
    return {
      ...this.defaultCardProps,
      title: 'Dividend Goals',
      cols: 4,
      rows: 3,
      desiredPositionByIssuer: {}
    };
  }
}
