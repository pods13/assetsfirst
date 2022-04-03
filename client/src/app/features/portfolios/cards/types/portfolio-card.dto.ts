import { GridsterItem } from 'angular-gridster2';
import { CardContainerType } from './card-container-type';

export interface PortfolioCardDto extends GridsterItem {
  id: string;
  containerType: CardContainerType;
  title?: string;
}
