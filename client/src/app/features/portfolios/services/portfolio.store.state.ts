import { PortfolioCardDto } from '../cards/types/portfolio-card.dto';

export class PortfolioStoreState {
  id!: number;
  cards: PortfolioCardDto[] = [];
}
