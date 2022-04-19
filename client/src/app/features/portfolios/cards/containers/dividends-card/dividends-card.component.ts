import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

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
    </ngx-charts-bar-vertical-2d>
  `,
  styleUrls: ['./dividends-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendsCardComponent implements OnInit, CardContainer<any, any> {

  card!: any;
  data$!: Observable<any>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
