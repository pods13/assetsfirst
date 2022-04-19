import { PortfolioCardDto } from '../types/portfolio-card.dto';

export class PortfolioCardStoreState {
  id!: number;
  cards: PortfolioCardDto[] = [];
}
