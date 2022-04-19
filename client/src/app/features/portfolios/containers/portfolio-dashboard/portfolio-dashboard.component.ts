import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { GRIDSTER_CONFIG } from './portfolio-dashboard.configs';
import { PortfolioCardDto } from '../../cards/types/portfolio-card.dto';
import { CardContainerType } from '../../cards/types/card-container-type';
import { PortfolioCardStore } from '../../cards/services/portfolio-card.store';
import { createCard } from '../../cards/helpers/create-card.helper';
import { PortfolioService } from '../../services/portfolio.service';
import { first, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-portfolio-dashboard',
  template: `
    <app-portfolio-actions-bar (addNewCard)="addNewCard($event)"></app-portfolio-actions-bar>
    <gridster [options]="options">
      <ng-container *ngIf="store.cardsByItems$ | async as cardsByItems">
        <gridster-item *ngFor="let card of cardsByItems.cards; let i = index; trackBy: trackByCardId"
                       [item]="cardsByItems.items[i]">
          <app-card-wrapper [card]="card">
          </app-card-wrapper>
        </gridster-item>
      </ng-container>
    </gridster>
  `,
  styleUrls: ['./portfolio-dashboard.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PortfolioDashboardComponent implements OnInit {
  options = {...GRIDSTER_CONFIG, itemChangeCallback: (card: any) => this.store.updateCard(card)};

  constructor(public store: PortfolioCardStore,
              private portfolioService: PortfolioService) {

  }

  ngOnInit(): void {
    this.portfolioService.getUserPortfolios().pipe(
      switchMap(portfolios => portfolios.length ? of(portfolios[0]) : this.portfolioService.addPortfolio({cards: []})),
      first(),
    ).subscribe(portfolio => this.store.init(portfolio.id, portfolio.cards));
  }

  trackByCardId(index: number, card: PortfolioCardDto) {
    if (!card) {
      return null;
    }
    return card.id;
  }

  addNewCard(cardType: CardContainerType) {
    this.store.addCard(createCard(cardType));
  }
}
