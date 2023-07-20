import { InstrumentType } from './instrument-type';

export interface InstrumentDto {
  id: number;
  ticker: string;
  name: string;
  instrumentType: InstrumentType;
  currencyCode: string;
}
