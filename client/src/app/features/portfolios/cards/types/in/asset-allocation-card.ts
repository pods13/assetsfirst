import { DashboardCard } from '../dashboard-card';
import { TagWithCategoryDto } from '@core/types/tag/tag.dto';

export interface AssetAllocationCard extends DashboardCard {
  allocatedBy: AllocatedByOption;
  customSegments?: CustomSegment[];
}

export interface CustomSegment {
  name: string;
  tags: TagWithCategoryDto[];
}

export enum AllocatedByOption {
  INSTRUMENT_TYPE = 'INSTRUMENT_TYPE',
  BROKER = 'BROKER',
  TRADING_CURRENCY = 'TRADING_CURRENCY',
  CUSTOM = 'CUSTOM',
}
