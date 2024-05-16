import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {GRIDSTER_CONFIG} from './portfolio-dashboard.configs';
import {DashboardCard} from '../../cards/types/dashboard-card';
import {CardContainerType} from '../../cards/types/card-container-type';
import {DashboardCardStore} from '../../cards/services/dashboard-card.store';
import {createCard} from '../../cards/helpers/create-card.helper';
import {PortfolioDashboardService} from '../../services/portfolio-dashboard.service';
import {first} from 'rxjs';

@Component({
    selector: 'app-portfolio-dashboard',
    template: `
    <app-dashboard-actions-bar (addNewCard)="addNewCard($event)"></app-dashboard-actions-bar>
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

    constructor(public store: DashboardCardStore,
                private dashboardService: PortfolioDashboardService) {

    }

    ngOnInit(): void {
        this.dashboardService.getUserPortfolio().pipe(
            first(),
        ).subscribe(portfolio => this.store.init(portfolio.id, portfolio.cards));
    }

    trackByCardId(index: number, card: DashboardCard) {
        if (!card) {
            return null;
        }
        return card.id;
    }

    addNewCard(cardType: CardContainerType) {
        this.store.addCard(createCard(cardType));
    }
}
