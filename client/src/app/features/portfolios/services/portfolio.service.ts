import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../auth/services/auth.service';
import { switchMap } from 'rxjs';
import { PortfolioDto } from '../types/portfolio.dto';
import { AddPortfolioDto } from '../types/add-portfolio.dto';

@Injectable()
export class PortfolioService {

  constructor(private http: HttpClient) {
  }

  getUserPortfolios() {
    return this.http.get<PortfolioDto[]>('/portfolios');
  }

  addPortfolio(dto: AddPortfolioDto) {
    return this.http.post<PortfolioDto>('/portfolios', dto);
  }

}
