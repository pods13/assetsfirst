import { CardData } from '../card-data';

export interface ContributionCardData extends CardData{
  xaxis: string[];
  contributions: Contribution[];
}

export interface Contribution {
  name: string;
  data: number[];
}
