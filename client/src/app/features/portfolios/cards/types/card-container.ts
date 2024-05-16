import {DashboardCard} from './dashboard-card';
import {Observable} from 'rxjs';
import {CardData} from './card-data';
import {EventEmitter} from '@angular/core';

export interface CardContainer<T1 extends DashboardCard, T2 extends CardData> {
    card: T1;
    data$: Observable<T2>;

    cardChanges$?: EventEmitter<T1>;
}
