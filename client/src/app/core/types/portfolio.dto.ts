import { PortfolioValuesByDates } from './portfolio-values-by-dates';

export interface PortfolioDto {
  valueIncreasePct: number;
  currencySymbol: string;
  investedAmountByDates: PortfolioValuesByDates;
}
