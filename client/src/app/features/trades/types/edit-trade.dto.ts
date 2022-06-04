import { InstrumentType } from './instrument-type';
import { TradeOperation } from './trade-operation';

export interface EditTradeDto {
  id: number;
  instrumentId: number;
  instrumentType: InstrumentType;

  price: number;
  quantity: number;
  date: Date;
  brokerId: number;
}
