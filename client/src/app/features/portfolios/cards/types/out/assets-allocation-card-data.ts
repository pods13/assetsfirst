import {CardData} from '../card-data';

export interface AssetsAllocationCardData extends CardData {
    segments: any[];
    currentTotalValue: number;
}
