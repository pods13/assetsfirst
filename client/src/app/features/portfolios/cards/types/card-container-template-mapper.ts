import { AllocationCardComponent } from '../containers/allocation-card/allocation-card.component';
import { CardContainerType } from './card-container-type';
import { DividendsCardComponent } from '../containers/dividends-card/dividends-card.component';

export const cardContainerTemplateMapper = {
  [CardContainerType.ALLOCATION]: AllocationCardComponent,
  [CardContainerType.DIVIDENDS]: DividendsCardComponent,
};
