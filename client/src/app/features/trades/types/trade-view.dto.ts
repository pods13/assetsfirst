import { TradeOperation } from './trade-operation';
import { InstrumentType } from './instrument-type';

export interface TradeViewDto {
  id: number;
  instrumentId: number;
  instrumentType: InstrumentType;
  symbol: string;
  exchange: string;
  name: string;
  date: Date;
  operation: TradeOperation;
  price: number;
  quantity: number;
  brokerId: number;
  brokerName: string;
  currencyCode: string;
}
