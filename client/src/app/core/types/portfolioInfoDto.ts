import {PortfolioValuesByDates} from './portfolio-values-by-dates';

export interface PortfolioInfoDto {
    valueIncreasePct: number;
    currencyCode: string;
    investedValueByDates: PortfolioValuesByDates;
    marketValueByDates: PortfolioValuesByDates;
}
