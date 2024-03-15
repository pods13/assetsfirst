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
  stack: string;
  value: number;
  details: DividendDetails[];
  currencyCode: string;
}

export interface DividendDetails {
  name: string;
  recordDate: Date;
  forecasted: boolean;
  total: number;
  currency: string;
}
