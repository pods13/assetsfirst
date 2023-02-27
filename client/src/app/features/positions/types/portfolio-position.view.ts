import { Ticker } from '../../../core/types/ticker';
import { SelectedTagDto } from './tag/selected-tag.dto';

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
  tags: SelectedTagDto[];
}
