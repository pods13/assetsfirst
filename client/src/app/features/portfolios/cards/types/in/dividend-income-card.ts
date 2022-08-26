import { DashboardCard } from '../dashboard-card';

export interface DividendIncomeCard extends DashboardCard {
  timeFrame: TimeFrameOption;
}

export enum TimeFrameOption {
  MONTH = 'MONTH',
  QUARTER = 'QUARTER',
  YEAR = 'YEAR'
}
