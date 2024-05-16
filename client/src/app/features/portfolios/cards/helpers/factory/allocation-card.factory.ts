import {CardFactory} from '../card-factory';
import {CardContainerType} from '../../types/card-container-type';
import {AllocatedByOption, AssetAllocationCard} from '../../types/in/asset-allocation-card';

export class AllocationCardFactory extends CardFactory<AssetAllocationCard> {

    constructor(containerType: CardContainerType) {
        super(containerType);
    }

    create(): AssetAllocationCard {
        return {
            ...this.originCard,
            title: 'Allocation',
            cols: 4,
            rows: 3,
            allocatedBy: AllocatedByOption.INSTRUMENT_TYPE
        };
    }
}
