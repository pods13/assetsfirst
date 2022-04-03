import { generateId } from './id-generator.helper';
import { PortfolioCardDto } from '../types/portfolio-card.dto';
import { CardContainerType } from '../types/card-container-type';

export abstract class CardFactory<T extends PortfolioCardDto> {
  protected defaultCardProps: PortfolioCardDto;

  protected constructor(containerType: CardContainerType) {
    this.defaultCardProps = {
      id: generateId(),
      x: 0,
      y: 0,
      cols: 3,
      rows: 2,
      containerType,
      minItemCols: 3,
      minItemRows: 2,
    };
  }

  abstract create(): T;
}
