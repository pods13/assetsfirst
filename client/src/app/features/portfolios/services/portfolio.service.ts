import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioDto } from '../types/portfolio.dto';

@Injectable()
export class PortfolioService {

  constructor(private http: HttpClient) {
  }

  getUserPortfolio() {
    return this.http.get<PortfolioDto>('/portfolios');
  }
}
