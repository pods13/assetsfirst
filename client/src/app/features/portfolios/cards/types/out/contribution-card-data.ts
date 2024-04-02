import {CardData} from '../card-data';

export interface ContributionCardData extends CardData {
    xaxis: string[];
    contributions: Contribution[];
    estimatedDividends: number;
    contributed: number;
    currencyCode: string;
}

export interface Contribution {
    name: string;
    data: number[];
}
