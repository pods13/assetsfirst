import { generateId } from './id-generator.helper';
import { PortfolioCard } from '../types/portfolio-card';
import { CardContainerType } from '../types/card-container-type';

export abstract class CardFactory<T extends PortfolioCard> {
  protected defaultCardProps: PortfolioCard;

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
