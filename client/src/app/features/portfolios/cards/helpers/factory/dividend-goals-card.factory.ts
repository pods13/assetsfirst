import { CardFactory } from '../card-factory';
import { PortfolioCardDto } from '../../types/portfolio-card.dto';
import { CardContainerType } from '../../types/card-container-type';

export class DividendGoalsCardFactory extends CardFactory<PortfolioCardDto> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): PortfolioCardDto {
    return {
      ...this.defaultCardProps,
      title: 'Dividend Goals',
      cols: 4,
      rows: 3,
    };
  }
}
