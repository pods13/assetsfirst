import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dividends-card',
  template: `
    <ngx-charts-bar-vertical-2d appFitChart
                                [results]="multi"
                                [scheme]="'vivid'"
                                [xAxis]="true"
                                [yAxis]="true"
                                [showYAxisLabel]="true" [yAxisLabel]="'hello'"
                                [showGridLines]="true"
                                [roundDomains]="true"
                                [noBarWhenZero]="false">
    </ngx-charts-bar-vertical-2d>
  `,
  styleUrls: ['./dividends-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendsCardComponent implements OnInit, CardContainer<any> {

  card!: any;
  data$!: Observable<any>;

  multi = [
    {
      "name": "Q1",
      "series": [
        {
          "name": "2010",
          "value": 7300000
        },
        {
          "name": "2011",
          "value": 8940000
        }
      ]
    },

    {
      "name": "Q2",
      "series": [
        {
          "name": "2010",
          "value": 7870000
        },
        {
          "name": "2011",
          "value": 8270000
        }
      ]
    },
  ];

  constructor() {
  }

  ngOnInit(): void {
  }

}
