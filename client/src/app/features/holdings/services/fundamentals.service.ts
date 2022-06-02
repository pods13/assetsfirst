import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioHoldingDto } from '../types/portfolio-holding,dto';

@Injectable()
export class FundamentalsService {

  constructor(private http: HttpClient) {
  }

  getHoldingsFundamentals() {
    return this.http.get<any[]>(`/fundamentals`);
  }

}
