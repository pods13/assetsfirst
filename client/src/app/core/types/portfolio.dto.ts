import {PortfolioValuesByDates} from './portfolio-values-by-dates';

export interface PortfolioDto {
    valueIncreasePct: number;
    currencyCode: string;
    investedValueByDates: PortfolioValuesByDates;
    marketValueByDates: PortfolioValuesByDates;
}
