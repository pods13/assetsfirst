import { AllocationCardComponent } from '../containers/allocation-card/allocation-card.component';
import { CardContainerType } from './card-container-type';

export const cardContainerTemplateMapper = {
  [CardContainerType.ALLOCATION]: AllocationCardComponent,
};
