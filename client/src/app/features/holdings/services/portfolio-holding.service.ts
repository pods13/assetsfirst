import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioHoldingDto } from '../types/portfolio-holding,dto';
import { PortfolioHoldingView } from '../types/portfolio-holding.view';

@Injectable()
export class PortfolioHoldingService {

  constructor(private http: HttpClient) {
  }

  getPortfolioHoldings() {
    return this.http.get<PortfolioHoldingDto[]>(`/portfolio-holdings`);
  }

  getPortfolioHoldingsView() {
    return this.http.get<PortfolioHoldingView[]>(`/portfolio-holdings/view`);
  }

}
