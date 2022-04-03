import { TradeCategory } from './trade-category';

export interface AddTradeDto {
  tradeCategory: TradeCategory;
  date: Date;
}
