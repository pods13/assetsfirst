import {CardData} from '../card-data';
import {Ticker} from "@core/types/ticker";

export interface AssetDisposalCardData extends CardData {

    losses: number;
    lossDetails: AssetDisposalDetails[];
    profits: number;
    profitDetails: AssetDisposalDetails[];
    taxableIncome: number;
    trackedYears: number[];
    currencyCode: string;
}

export interface AssetDisposalDetails {
    ticker: Ticker;
    total: number;
}
