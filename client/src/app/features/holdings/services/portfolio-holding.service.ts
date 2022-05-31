import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioHoldingDto } from '../types/portfolio-holding,dto';

@Injectable()
export class PortfolioHoldingService {

  constructor(private http: HttpClient) {
  }

  getPortfolioHoldings() {
    return this.http.get<PortfolioHoldingDto[]>(`/portfolio-holdings`);
  }

}
