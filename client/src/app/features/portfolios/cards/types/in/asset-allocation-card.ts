import { DashboardCard } from '../dashboard-card';

export interface AssetAllocationCard extends DashboardCard {
  allocatedBy: AllocatedByOption;
}

export enum AllocatedByOption {
  INSTRUMENT_TYPE = 'INSTRUMENT_TYPE',
  BROKER = 'BROKER'
}
