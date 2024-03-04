import {InstrumentType} from './instrument-type';

export interface EditTradeDto {
    tradeId: number;
    instrumentId: number;
    instrumentType: InstrumentType;
    price: number;
    quantity: number;
    date: Date;
    intermediaryId: number;
}
