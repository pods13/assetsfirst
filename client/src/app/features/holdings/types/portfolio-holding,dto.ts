import { TickerSymbol } from '../../../core/types/ticker-symbol';

export interface PortfolioHoldingDto {
  identifier: TickerSymbol;
  quantity: number;
  price: number;
}
