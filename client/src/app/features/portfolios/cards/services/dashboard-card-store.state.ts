import { DashboardCard } from '../types/dashboard-card';

export class DashboardCardStoreState {
  id!: number;
  cards: DashboardCard[] = [];
}
