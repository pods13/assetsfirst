import { AllocationCardComponent } from '../containers/allocation-card/allocation-card.component';
import { CardContainerType } from './card-container-type';
import { DividendIncomeCardComponent } from '../containers/dividend-income-card/dividend-income-card.component';
import { DividendGoalsCardComponent } from '../containers/dividend-goals-card/dividend-goals-card.component';
import {
  SectoralDistributionCardComponent
} from '../containers/sectoral-distribution-card/sectoral-distribution-card.component';
import { BalanceCardComponent } from '../containers/balance-card/balance-card.component';
import { ContributionCardComponent } from '../containers/contribution-card/contribution-card.component';

export const cardContainerTemplateMapper = {
  [CardContainerType.ALLOCATION]: AllocationCardComponent,
  [CardContainerType.DIVIDEND_INCOME]: DividendIncomeCardComponent,
  [CardContainerType.DIVIDEND_GOALS]: DividendGoalsCardComponent,
  [CardContainerType.SECTORAL_DISTRIBUTION]: SectoralDistributionCardComponent,
  [CardContainerType.BALANCE]: BalanceCardComponent,
  [CardContainerType.CONTRIBUTION]: ContributionCardComponent
};
