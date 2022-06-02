import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class PortfolioService {
  constructor(private http: HttpClient) {
  }

  getMarketValue() {
    return this.http.get<number>(`/portfolios/calculateMarketValue`);
  }
}
