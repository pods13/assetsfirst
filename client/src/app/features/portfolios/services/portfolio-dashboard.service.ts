import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioDashboardDto } from '../types/portfolio-dashboard.dto';

@Injectable()
export class PortfolioDashboardService {

  constructor(private http: HttpClient) {
  }

  getUserPortfolio() {
    return this.http.get<PortfolioDashboardDto>('/portfolio-dashboards');
  }
}
