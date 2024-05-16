import {GridsterItem} from 'angular-gridster2';
import {CardContainerType} from './card-container-type';

export interface DashboardCard extends GridsterItem {
    id: string;
    containerType: CardContainerType;
    title?: string;
}
