import { TickerSymbol } from '../../../core/types/ticker-symbol';

export interface PortfolioHoldingView {
  id: number;
  instrumentId: number;
  instrumentType: any;
  identifier: TickerSymbol;
  quantity: number;
  price: number;
  currencySymbol: string;
  pctOfPortfolio: number;
  marketValue: number;
}
