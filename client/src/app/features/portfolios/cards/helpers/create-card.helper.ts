import { AllocationCardFactory } from './factory/allocation-card.factory';
import { CardContainerType } from '../types/card-container-type';
import { DashboardCard } from '../types/dashboard-card';
import { DividendsCardFactory } from './factory/dividends-card.factory';
import { DividendGoalsCardFactory } from './factory/dividend-goals-card.factory';
import { SectoralDistributionCardFactory } from './factory/sectoral-distribution-card.factory';


export function createCard(containerType: CardContainerType): DashboardCard {
  const cardFactory = cardContainerTypeByFactory[containerType];
  if (!cardFactory) {
    throw new Error(`Cannot find card factory for ${containerType}`);
  }
  return new cardFactory(containerType).create();
}

const cardContainerTypeByFactory = {
  [CardContainerType.ALLOCATION]: AllocationCardFactory,
  [CardContainerType.DIVIDENDS]: DividendsCardFactory,
  [CardContainerType.DIVIDEND_GOALS]: DividendGoalsCardFactory,
  [CardContainerType.SECTORAL_DISTRIBUTION]: SectoralDistributionCardFactory,
};
