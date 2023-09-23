import { CardContainerType } from '../types/card-container-type';
import { EditAllocationCardComponent } from '../components/edit-allocation-card/edit-allocation-card.component';

export const editCardDialogByContainerType = {
  [CardContainerType.ALLOCATION]: EditAllocationCardComponent,
};
