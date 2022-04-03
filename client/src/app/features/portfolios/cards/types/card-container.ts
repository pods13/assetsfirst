import { Input } from '@angular/core';
import { PortfolioCardDto } from './portfolio-card.dto';
import { Observable } from 'rxjs';

export interface CardContainer<T1 extends PortfolioCardDto> {
  card: T1;
  data$: Observable<any>;
}
