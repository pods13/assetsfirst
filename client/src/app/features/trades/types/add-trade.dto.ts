import { InstrumentType } from './instrument-type';
import { TradeOperation } from './trade-operation';

export interface AddTradeDto {
  instrumentId: number;
  operation: TradeOperation;
  price: number;
  quantity: number;
  instrumentType: InstrumentType;
  date: Date;
}
