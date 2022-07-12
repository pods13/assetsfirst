import { InstrumentType } from './instrument-type';

export interface DeleteTradeDto {
  tradeId: number;
  instrumentId: number;
  instrumentType: InstrumentType;
}
