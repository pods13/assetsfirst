import { PortfolioCardDto } from '../cards/types/portfolio-card.dto';

export interface PortfolioDto {
  id: number;
  cards: PortfolioCardDto[];
}
