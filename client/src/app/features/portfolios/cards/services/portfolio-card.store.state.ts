import { PortfolioCard } from '../types/portfolio-card';

export class PortfolioCardStoreState {
  id!: number;
  cards: PortfolioCard[] = [];
}
