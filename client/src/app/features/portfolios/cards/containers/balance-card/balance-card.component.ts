import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { BalanceCard } from '../../types/in/balance-card';
import { BalanceCardData } from '../../types/out/balance-card-data';
import { lightColor } from '../../helpers/chart-color-sets';

@Component({
  selector: 'app-balance-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card?.title }}</h2>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <div class="current-amount">{{data.currentAmount | currency: data.currencySymbol}}</div>
      <div class="invested-amount">
        <span>{{data.investedAmount | currency: data.currencySymbol}}</span>
        <span>{{'invested'}}</span>
      </div>
      <ngx-charts-area-chart [scheme]="colorScheme"
                             [results]="[data.investedAmountByDates]"
                             [view]="[card.cols * 100, card.rows * 100 - 5]"
                             [xAxis]="true"
                             [yAxis]="true"
                             [showGridLines]="true"
                             [showXAxisLabel]="true"
                             [showYAxisLabel]="false"
                             [autoScale]="true"
                             [legend]="false">
      </ngx-charts-area-chart>
    </ng-container>
  `,
  styleUrls: ['./balance-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BalanceCardComponent implements OnInit, CardContainer<BalanceCard, BalanceCardData> {

  card!: BalanceCard;
  data$!: Observable<BalanceCardData>;

  colorScheme = lightColor;

  constructor() {
  }

  ngOnInit(): void {
    this.data$.subscribe(data => {
      console.log(data.investedAmountByDates);
    })
  }

}
