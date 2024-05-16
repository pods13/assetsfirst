import {Store} from '@core/services/store';
import {PositionStoreState} from './position-store.state';
import {Injectable} from '@angular/core';
import {PortfolioPositionView} from '../types/portfolio-position.view';
import {TagWithCategoryDto} from '@core/types/tag/tag.dto';
import {Sort} from '@angular/material/sort';

@Injectable()
export class PositionStore extends Store<PositionStoreState> {

    constructor() {
        super(new PositionStoreState());
    }

    init(positions: PortfolioPositionView[], sort: Sort) {
        const sortedPositions = this.sortPositions(positions, sort);
        this.setState({...this.state, positions: sortedPositions});
    }

    updatePositionTags(positionId: number, selectedTags: TagWithCategoryDto[]) {
        const positionsNewState = this.state.positions
            .map(p => p.id === positionId ? {...p, tags: selectedTags} : p);

        this.setState({...this.state, positions: positionsNewState});
    }

    private sortPositions(positions: PortfolioPositionView[], sort: Sort) {
        return positions.sort((a, b) => {
            const sortPropName = sort.active as keyof typeof a;
            let prop1 = a[sortPropName];
            let prop2 = b[sortPropName];
            if (sort.direction === 'desc') {
                [prop1, prop2] = [prop2, prop1];
            }
            return typeof prop1 === 'string' ? prop1.localeCompare(prop2) : prop1 - prop2;
        });
    }

    reorderExistingPositions(sort: Sort) {
        this.init(this.state.positions, sort);
    }
}
