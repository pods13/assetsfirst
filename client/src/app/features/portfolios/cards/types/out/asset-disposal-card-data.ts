import { CardData } from '../card-data';

export interface AssetDisposalCardData extends CardData {

  losses: number[];
  profits: number[];
  taxableIncome: number[];
  trackedYears: number[];
}
