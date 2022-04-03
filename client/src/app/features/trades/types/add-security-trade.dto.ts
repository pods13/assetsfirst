import { TradeOperation } from './trade-operation';
import { AddTradeDto } from './add-trade.dto';

export interface AddSecurityTradeDto extends AddTradeDto {
  securityId: number;
  operation: TradeOperation;
  price: number;
  quantity: number;
}
