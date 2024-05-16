import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {CardContainer} from '../../types/card-container';
import {InvestmentYieldCard} from '../../types/in/investment-yield-card';
import {InvestmentYieldCardData} from '../../types/out/investment-yield-card-data';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-investment-yield-card',
    template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <div class="yield"><span>TTM yield on cost: </span>{{data.yieldOnCost + '%'}}</div>
      <div class="yield"><span>TTM dividend yield: </span>{{data.dividendYield + '%'}}</div>
    </ng-container>
  `,
    styleUrls: ['./investment-yield-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvestmentYieldCardComponent implements CardContainer<InvestmentYieldCard, InvestmentYieldCardData>, OnInit {

    card!: InvestmentYieldCard;
    data$!: Observable<InvestmentYieldCardData>;

    constructor() {
    }

    ngOnInit(): void {
    }


}
