import { CardFactory } from '../card-factory';
import { PortfolioCardDto } from '../../types/portfolio-card.dto';
import { CardContainerType } from '../../types/card-container-type';

export class AllocationCardFactory extends CardFactory<PortfolioCardDto> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): PortfolioCardDto {
    return {
      ...this.defaultCardProps,
      title: 'Allocation',
      cols: 4,
      rows: 3,
    };
  }
}
