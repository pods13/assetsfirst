import {DashboardCard} from '../dashboard-card';

export interface DividendIncomeCard extends DashboardCard {
    timeFrame: TimeFrameOption;
    useCustomDividendProjections?: boolean;
    annualDividendProjections?: AnnualDividendProjection[];
}

export enum TimeFrameOption {
    MONTH = 'MONTH',
    QUARTER = 'QUARTER',
    YEAR = 'YEAR'
}

export interface AnnualDividendProjection {
    ticker: string;
    dividend: number;
    currency: string;
}
