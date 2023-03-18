import { CardData } from '../card-data';

export interface BalanceCardData extends CardData {
  currentAmount: number;
  investedAmount: number;
  currencySymbol: string;
  investedAmountByDates: TimeFrameSummary;
}

interface TimeFrameSummary {
  xaxis: string[];
  values: number[];
}
