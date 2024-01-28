import {CardData} from '../card-data';
import {PortfolioValuesByDates} from '@core/types/portfolio-values-by-dates';

export interface BalanceCardData extends CardData {
    currentAmount: number;
    investedAmount: number;
    currencyCode: string;
    investedValueByDates: PortfolioValuesByDates;
}
