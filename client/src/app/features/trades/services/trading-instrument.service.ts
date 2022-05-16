import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class TradingInstrumentService {

  constructor(private http: HttpClient) {
  }

  searchInstrumentsByNameOrTicker(searchTerm: string) {
    return this.http.get<any[]>(`/instruments?search=${searchTerm}`);
  }

}
