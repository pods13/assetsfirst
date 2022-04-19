import { CardFactory } from '../card-factory';
import { PortfolioCard } from '../../types/portfolio-card';
import { CardContainerType } from '../../types/card-container-type';

export class DividendsCardFactory extends CardFactory<PortfolioCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): PortfolioCard {
    return {
      ...this.defaultCardProps,
      title: 'Dividends',
      cols: 4,
      rows: 3,
    };
  }
}
