import { CardFactory } from '../card-factory';
import { DashboardCard } from '../../types/dashboard-card';
import { CardContainerType } from '../../types/card-container-type';

export class SectoralDistributionCardFactory extends CardFactory<DashboardCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): DashboardCard {
    return {
      ...this.originCard,
      title: 'Sectoral Distribution',
      cols: 4,
      rows: 3,
    };
  }
}
