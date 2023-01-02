import { Ticker } from '../../../core/types/ticker';

export interface PortfolioPositionView {
  id: number;
  instrumentId: number;
  instrumentType: any;
  identifier: Ticker;
  quantity: number;
  price: number;
  currencySymbol: string;
  pctOfPortfolio: number;
  marketValue: number;
  yieldOnCost: number;
}
