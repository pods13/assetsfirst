import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { PortfolioCard } from '../../types/portfolio-card';
import { DividendIncomeCardData } from '../../types/out/dividend-income-card-data';

@Component({
  selector: 'app-dividends-card',
  template: `
    <ngx-charts-bar-vertical-2d appFitChart *ngIf="data$ | async as data"
                                [results]="data.dividends"
                                [scheme]="'vivid'"
                                [xAxis]="true"
                                [yAxis]="true"
                                [showYAxisLabel]="true" [yAxisLabel]="'Earned Dividends'"
                                [showGridLines]="true"
                                [roundDomains]="true"
                                [noBarWhenZero]="false">
      <ng-template #tooltipTemplate let-model="model">
        <div class="tooltip">
          <div class="tooltip-title">{{model.series + '·' + model.name + ': ' + model.value}}</div>
          <div class="tooltip-details">
          <span class="dividend-detail" *ngFor="let div of model.details">
            {{div.name + ': ' + div.total}}
          </span>
          </div>
        </div>
      </ng-template>
    </ngx-charts-bar-vertical-2d>
  `,
  styleUrls: ['./dividends-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendsCardComponent implements OnInit, CardContainer<PortfolioCard, DividendIncomeCardData> {

  card!: PortfolioCard;
  data$!: Observable<DividendIncomeCardData>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
