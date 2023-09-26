import { InstrumentType } from './instrument-type';

export interface InstrumentDto {
  id: number;
  symbol: string;
  name: string;
  instrumentType: InstrumentType;
  currencyCode: string;
}
