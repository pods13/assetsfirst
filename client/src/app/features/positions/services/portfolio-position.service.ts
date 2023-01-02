import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PortfolioPositionDto } from '../types/portfolio-position,dto';
import { PortfolioPositionView } from '../types/portfolio-position.view';

@Injectable()
export class PortfolioPositionService {

  constructor(private http: HttpClient) {
  }

  getPortfolioPositions() {
    return this.http.get<PortfolioPositionDto[]>(`/portfolio-positions`);
  }

  getPortfolioPositionsView() {
    return this.http.get<PortfolioPositionView[]>(`/portfolio-positions/view`);
  }

}
