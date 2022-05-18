import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AddTradeDto } from '../types/add-trade.dto';
import { InstrumentType } from '../types/instrument-type';

@Injectable()
export class TradeService {

  constructor(private http: HttpClient) {
  }

  getUserTrades() {
    return this.http.get('/trades');
  }

  addTrade(dto: AddTradeDto) {
    const urlPrefix = this.getUrlPrefixByType(dto.instrumentType);
    return this.http.post(`/${urlPrefix}/trades`, dto);
  }

  private getUrlPrefixByType(instrumentType: InstrumentType): string {
    if (InstrumentType.FX === instrumentType) {
      return instrumentType.toLowerCase();
    }
    return instrumentType.toLowerCase() + 's';
  }
}
