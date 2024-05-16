import {CardFactory} from '../card-factory';
import {DashboardCard} from '../../types/dashboard-card';
import {CardContainerType} from '../../types/card-container-type';
import {InvestmentYieldCard} from '../../types/in/investment-yield-card';

export class InvestmentYieldCardFactory extends CardFactory<InvestmentYieldCard> {

    constructor(containerType: CardContainerType) {
        super(containerType);
    }

    create(): DashboardCard {
        return {
            ...this.originCard,
            title: 'Investment Yield',
            cols: 2,
            rows: 1,
            minItemCols: 2,
            minItemRows: 1,
        };
    }
}
