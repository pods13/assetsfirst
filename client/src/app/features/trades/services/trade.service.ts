import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AddTradeDto } from '../types/add-trade.dto';
import { TradeCategory } from '../types/trade-category';

@Injectable()
export class TradeService {

  constructor(private http: HttpClient) {
  }

  getUserTrades() {
    return this.http.get('/trades');
  }

  addTrade(dto: AddTradeDto) {
    const tradeCategory = dto.tradeCategory.toLowerCase();
    const urlPrefix = tradeCategory + 's';
    return this.http.post(`/${urlPrefix}/trades`, dto);
  }
}
