import { AllocationCardComponent } from '../containers/allocation-card/allocation-card.component';
import { CardContainerType } from './card-container-type';
import { DividendsCardComponent } from '../containers/dividends-card/dividends-card.component';
import { DividendGoalsCardComponent } from '../containers/dividend-goals-card/dividend-goals-card.component';
import {
  SectoralDistributionCardComponent
} from '../containers/sectoral-distribution-card/sectoral-distribution-card.component';

export const cardContainerTemplateMapper = {
  [CardContainerType.ALLOCATION]: AllocationCardComponent,
  [CardContainerType.DIVIDENDS]: DividendsCardComponent,
  [CardContainerType.DIVIDEND_GOALS]: DividendGoalsCardComponent,
  [CardContainerType.SECTORAL_DISTRIBUTION]: SectoralDistributionCardComponent,
};
