import { PortfolioCard } from '../portfolio-card';

export interface DividendGoalsCard extends PortfolioCard {
  desiredPositionByIssuer: {[key: string]: number};
}
