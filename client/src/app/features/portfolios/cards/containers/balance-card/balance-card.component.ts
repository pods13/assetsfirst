import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { BalanceCard } from '../../types/in/balance-card';
import { BalanceCardData } from '../../types/out/balance-card-data';

@Component({
  selector: 'app-balance-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <div class="current-amount">{{data.currentAmount | currency: data.currencySymbol}}</div>
      <div class="invested-amount">
        <span>{{data.investedAmount | currency: data.currencySymbol}}</span>
        <span>{{'invested'}}</span>
      </div>
    </ng-container>
  `,
  styleUrls: ['./balance-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BalanceCardComponent implements OnInit, CardContainer<BalanceCard, BalanceCardData> {

  card!: BalanceCard;
  data$!: Observable<BalanceCardData>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
