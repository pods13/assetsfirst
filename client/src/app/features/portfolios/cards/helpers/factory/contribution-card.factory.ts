import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { ContributionCard } from '../../types/in/contribution-card';

export class ContributionCardFactory extends CardFactory<ContributionCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): ContributionCard {
    return {
      ...this.originCard,
      title: 'Contributions',
      cols: 3,
      rows: 2,
      minItemCols: 3,
      minItemRows: 2,
    };
  }
}
