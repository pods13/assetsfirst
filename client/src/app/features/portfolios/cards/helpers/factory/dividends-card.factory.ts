import { CardFactory } from '../card-factory';
import { DashboardCard } from '../../types/dashboard-card';
import { CardContainerType } from '../../types/card-container-type';

export class DividendsCardFactory extends CardFactory<DashboardCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): DashboardCard {
    return {
      ...this.originCard,
      title: 'Dividends',
      cols: 4,
      rows: 3,
    };
  }
}
