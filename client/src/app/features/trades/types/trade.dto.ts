import { TradeOperation } from './trade-operation';
import { InstrumentType } from './instrument-type';

export interface TradeDto {
  id: number;
  instrumentId: number;
  instrumentType: InstrumentType;
  ticker: string;
  name: string;
  date: Date;
  operation: TradeOperation;
  price: number;
  quantity: number;
}
