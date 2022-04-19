import { PortfolioCardDto } from './portfolio-card.dto';
import { Observable } from 'rxjs';
import { CardData } from './card-data';

export interface CardContainer<T1 extends PortfolioCardDto, T2 extends CardData> {
  card: T1;
  data$: Observable<T2>;

  tapIntoData?: (data: T2) => void;
}
