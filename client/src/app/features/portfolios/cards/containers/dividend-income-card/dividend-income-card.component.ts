import { ChangeDetectionStrategy, Component, EventEmitter, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { DividendIncomeCardData } from '../../types/out/dividend-income-card-data';
import { DividendIncomeCard, TimeFrameOption } from '../../types/in/dividend-income-card';
import { lightColor } from '../../helpers/chart-color-sets';

@Component({
  selector: 'app-dividends-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card?.title }}</h2>
      <mat-form-field appearance="legacy">
        <div matPrefix>{{'By:'}}</div>
        <mat-select [value]="card.timeFrame" (valueChange)="onTimeFrameOptionChanged($event)">
          <mat-option *ngFor="let timeFrame of timeFrameOptions"
                      [value]="timeFrame.value">
            {{timeFrame.value}}
          </mat-option>
        </mat-select>
      </mat-form-field>
    </div>
    <ngx-charts-bar-vertical-2d *ngIf="data$ | async as data" class="dividend-income-chart clearfix"
                                [results]="data.dividends"
                                [scheme]="colorScheme"
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
  styleUrls: ['./dividend-income-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendIncomeCardComponent implements OnInit, CardContainer<DividendIncomeCard, DividendIncomeCardData> {

  card!: DividendIncomeCard;
  data$!: Observable<DividendIncomeCardData>;

  cardChanges$ = new EventEmitter<DividendIncomeCard>();

  colorScheme = lightColor;

  timeFrameOptions = Object.keys(TimeFrameOption).map(timeFrame => ({value: timeFrame}));

  constructor() {
  }

  ngOnInit(): void {
  }

  onTimeFrameOptionChanged(timeFrame: TimeFrameOption) {
    this.cardChanges$.emit({...this.card, timeFrame});
  }
}
