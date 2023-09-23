import { Ticker } from '@core/types/ticker';
import { TagWithCategoryDto } from '../../../core/types/tag/tag.dto';

export interface PortfolioPositionView {
  id: number;
  companyName: string;
  instrumentId: number;
  instrumentType: any;
  identifier: Ticker;
  quantity: number;
  price: number;
  currencyCode: string;
  pctOfPortfolio: number;
  marketValue: number;
  yieldOnCost: number;
  tags: TagWithCategoryDto[];
  accumulatedDividends: number;
  realizedPnl: number;
}
