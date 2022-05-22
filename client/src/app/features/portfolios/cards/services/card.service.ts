import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DashboardCard } from '../types/dashboard-card';
import { RxStompService } from '../../../../core/services/rx-stomp.service';

@Injectable()
export class CardService {

  constructor(private http: HttpClient,
              private rxStompService: RxStompService) {
  }

  addCard(portfolioId: number, card: DashboardCard) {
    this.manageCard(portfolioId, card, 'add');
  }

  updateCard(portfolioId: number, card: DashboardCard) {
    this.manageCard(portfolioId, card, 'update');
  }

  deleteCard(portfolioId: number, card: DashboardCard) {
    this.manageCard(portfolioId, card, 'delete');
  }

  private manageCard(portfolioId: number, card: DashboardCard, operation: string) {
    this.rxStompService.publish({
      destination: `/app/${portfolioId}/cards/${operation}`,
      body: JSON.stringify(card),
    });
  }

}
