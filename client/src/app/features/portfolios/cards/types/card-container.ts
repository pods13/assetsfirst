import { DashboardCard } from './dashboard-card';
import { Observable } from 'rxjs';
import { CardData } from './card-data';

export interface CardContainer<T1 extends DashboardCard, T2 extends CardData> {
  card: T1;
  data$: Observable<T2>;
}
