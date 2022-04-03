import { MoneyTradeOperation } from './money-trade-operation';
import { TradeCategory } from '../trade-category';
import { AddTradeDto } from '../add-trade.dto';

export interface AddMoneyTradeDto extends AddTradeDto {
  operation: MoneyTradeOperation;
  amount: number;
  currencyCode: string;
}
