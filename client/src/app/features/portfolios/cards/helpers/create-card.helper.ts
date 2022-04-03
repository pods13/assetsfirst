import { AllocationCardFactory } from './factory/allocation-card.factory';
import { CardContainerType } from '../types/card-container-type';
import { PortfolioCardDto } from '../types/portfolio-card.dto';


export function createCard(containerType: CardContainerType): PortfolioCardDto {
  const cardFactory = cardContainerTypeByFactory[containerType];
  if (!cardFactory) {
    throw new Error(`Cannot find card factory for ${containerType}`);
  }
  return new cardFactory(containerType).create();
}

const cardContainerTypeByFactory = {
  [CardContainerType.ALLOCATION]: AllocationCardFactory
};
