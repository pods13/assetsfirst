import { Ticker } from '../../../core/types/ticker';

export interface PortfolioPositionDto {
  identifier: Ticker;
  quantity: number;
  price: number;
}
