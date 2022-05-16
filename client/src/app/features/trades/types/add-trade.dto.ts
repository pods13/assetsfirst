import { TradeCategory } from './trade-category';
import { TradeOperation } from './trade-operation';

export interface AddTradeDto {
  instrumentId: number;
  operation: TradeOperation;
  price: number;
  quantity: number;
  tradeCategory: TradeCategory;
  date: Date;
}
