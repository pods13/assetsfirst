import { Ticker } from '../../../core/types/ticker';

export interface PortfolioHoldingDto {
  identifier: Ticker;
  quantity: number;
  price: number;
}
