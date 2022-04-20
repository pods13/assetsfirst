import { generateId } from './id-generator.helper';
import { PortfolioCard } from '../types/portfolio-card';
import { CardContainerType } from '../types/card-container-type';

export const defaultCardProps = {
  x: 0,
  y: 0,
  cols: 3,
  rows: 2,
  minItemCols: 3,
  minItemRows: 2,
};

export abstract class CardFactory<T extends PortfolioCard> {
  protected originCard: PortfolioCard;

  protected constructor(containerType: CardContainerType) {
    this.originCard = {id: generateId(), containerType, ...defaultCardProps};
  }

  abstract create(): T;
}
