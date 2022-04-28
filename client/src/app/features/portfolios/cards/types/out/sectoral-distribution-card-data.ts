import { CardData } from '../card-data';

export interface SectoralDistributionCardData extends CardData {
  items: SectoralDistributionDataItem[];
}

interface SectoralDistributionDataItem {
  name: string;
  value: number;
  children?: SectoralDistributionDataItem[];
}
