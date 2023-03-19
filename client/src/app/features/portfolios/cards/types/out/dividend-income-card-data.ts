import { CardData } from '../card-data';

export interface DividendIncomeCardData extends CardData {
  dividends: TimeFrameDividend[];
}

export interface TimeFrameDividend {
  name: string;
  series: DividendSummary[];
}

export interface DividendSummary {
  name: string;
  value: number;
  details: DividendDetails[];
}

export interface DividendDetails {
  name: string;
  payDate: Date;
  forecasted: boolean;
  total: number;
  currency: string;
}
