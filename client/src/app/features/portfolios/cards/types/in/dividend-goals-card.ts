import { DashboardCard } from '../dashboard-card';

export interface DividendGoalsCard extends DashboardCard {
  desiredYieldByIssuer: {[key: string]: number};
}
