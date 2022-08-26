import { ChangeDetectionStrategy, Component, EventEmitter, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { DividendIncomeCardData } from '../../types/out/dividend-income-card-data';
import { DividendIncomeCard, TimeFrameOption } from '../../types/in/dividend-income-card';

@Component({
  selector: 'app-dividends-card',
  template: `
    <mat-form-field appearance="legacy">
      <div matPrefix>{{'By:'}}</div>
      <mat-select [value]="card.timeFrame" (valueChange)="onTimeFrameOptionChanged($event)">
        <mat-option *ngFor="let timeFrame of timeFrameOptions"
                    [value]="timeFrame.value">
          {{timeFrame.value}}
        </mat-option>
      </mat-select>
    </mat-form-field>
    <ngx-charts-bar-vertical-2d *ngIf="data$ | async as data" class="dividend-income-chart clearfix"
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
  styleUrls: ['./dividend-income-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendIncomeCardComponent implements OnInit, CardContainer<DividendIncomeCard, DividendIncomeCardData> {

  card!: DividendIncomeCard;
  data$!: Observable<DividendIncomeCardData>;

  cardChanges$ = new EventEmitter<DividendIncomeCard>();

  timeFrameOptions = Object.keys(TimeFrameOption).map(timeFrame => ({value: timeFrame}));

  constructor() {
  }

  ngOnInit(): void {
  }

  onTimeFrameOptionChanged(timeFrame: TimeFrameOption) {
    this.cardChanges$.emit({...this.card, timeFrame});
  }
}
