import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dividends-card',
  template: `
    <ngx-charts-bar-vertical-2d appFitChart *ngIf="data$ | async as data"
                                [results]="res"
                                [scheme]="'vivid'"
                                [xAxis]="true"
                                [yAxis]="true"
                                [showYAxisLabel]="true" [yAxisLabel]="'Earned Dividends'"
                                [showGridLines]="true"
                                [roundDomains]="true"
                                [animations]="false"
                                [noBarWhenZero]="false">
    </ngx-charts-bar-vertical-2d>
  `,
  styleUrls: ['./dividends-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendsCardComponent implements OnInit, CardContainer<any> {

  card!: any;
  data$!: Observable<any>;
  res = [{"name": "Q1", "series": [{"name": "2022", "value": 6776.513}]}, {
    "name": "Q2",
    "series": [{"name": "2020", "value": 18070}, {"name": "2021", "value": 9129.825}, {
      "name": "2022",
      "value": 7166.7
    }]
  }, {"name": "Q3", "series": [{"name": "2021", "value": 20219.825}]}, {
    "name": "Q4",
    "series": [{"name": "2021", "value": 2189.825}]
  }]


  constructor() {
  }

  ngOnInit(): void {

    this.data$.subscribe(res => console.log(JSON.stringify(res)));
  }

}
