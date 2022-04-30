import { PortfolioCard } from '../portfolio-card';

export interface DividendGoalsCard extends PortfolioCard {
  desiredYieldByIssuer: {[key: string]: number};
}
