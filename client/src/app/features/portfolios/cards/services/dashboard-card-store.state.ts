import {DashboardCard} from '../types/dashboard-card';

export class DashboardCardStoreState {
    dashboardId!: number;
    cards: DashboardCard[] = [];
}
