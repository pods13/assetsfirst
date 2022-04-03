import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { GRIDSTER_CONFIG } from './portfolio-dashboard.configs';
import { PortfolioCardDto } from '../../cards/types/portfolio-card.dto';
import { CardContainerType } from '../../cards/types/card-container-type';
import { PortfolioStore } from '../../services/portfolio.store';
import { createCard } from '../../cards/helpers/create-card.helper';

@Component({
  selector: 'app-portfolio-dashboard',
  template: `
    <app-portfolio-actions-bar (addNewCard)="addNewCard($event)"></app-portfolio-actions-bar>
    <gridster [options]="options">
        <ng-container *ngIf="store.cardsByItems$ | async as cardsByItems">
          <gridster-item *ngFor="let card of cardsByItems.cards; let i = index; trackBy: trackByCardId" [item]="cardsByItems.items[i]">
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

  constructor(public store: PortfolioStore) {

  }

  ngOnInit(): void {
    this.store.init()
      .subscribe();
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
