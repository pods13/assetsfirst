import { PortfolioCard } from '../cards/types/portfolio-card';

export interface PortfolioDto {
  id: number;
  cards: PortfolioCard[];
}
