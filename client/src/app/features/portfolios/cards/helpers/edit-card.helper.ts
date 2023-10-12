import { CardContainerType } from '../types/card-container-type';
import { EditAllocationCardComponent } from '../components/edit-allocation-card/edit-allocation-card.component';
import {
  EditDividendIncomeCardComponent
} from '../components/edit-dividend-income-card/edit-dividend-income-card.component';

export const editCardDialogByContainerType = {
  [CardContainerType.ALLOCATION]: EditAllocationCardComponent,
  [CardContainerType.DIVIDEND_INCOME]: EditDividendIncomeCardComponent,
};
