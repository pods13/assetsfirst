import { CardFactory } from '../card-factory';
import { PortfolioCard } from '../../types/portfolio-card';
import { CardContainerType } from '../../types/card-container-type';

export class SectoralDistributionCardFactory extends CardFactory<PortfolioCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): PortfolioCard {
    return {
      ...this.originCard,
      title: 'Sectoral Distribution',
      cols: 4,
      rows: 3,
    };
  }
}
