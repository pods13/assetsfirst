import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AddTradeDto } from '../types/add-trade.dto';
import { InstrumentType } from '../types/instrument-type';
import { EditTradeDto } from '../types/edit-trade.dto';
import { TradeDto } from '../types/trade.dto';
import { DeleteTradeDto } from '../types/delete-trade.dto';
import { Page } from '../../../core/types/page';
import { Pageable } from '../../../core/types/pageable';
import { toHttpParams } from '../../../core/helpers/pageable.helpers';

@Injectable()
export class TradeService {

  constructor(private http: HttpClient) {
  }

  getUserTrades(pageable: Pageable) {
    return this.http.get<Page<TradeDto[]>>('/trades', {params: toHttpParams(pageable)});
  }

  addTrade(dto: AddTradeDto) {
    const urlPrefix = this.getUrlPrefixByType(dto.instrumentType);
    return this.http.post(`/${urlPrefix}/trades`, dto);
  }

  editTrade(dto: EditTradeDto) {
    const urlPrefix = this.getUrlPrefixByType(dto.instrumentType);
    return this.http.patch(`/${urlPrefix}/trades`, dto);
  }

  private getUrlPrefixByType(instrumentType: InstrumentType): string {
    if (InstrumentType.FX === instrumentType) {
      return instrumentType.toLowerCase();
    }
    return instrumentType.toLowerCase() + 's';
  }

  deleteTrade(dto: DeleteTradeDto) {
    const urlPrefix = this.getUrlPrefixByType(dto.instrumentType);
    const {instrumentId, tradeId} = dto;
    return this.http.delete(`/${urlPrefix}/trades/${dto.tradeId}`, {
      body: {tradeId, instrumentId}
    });
  }
}
