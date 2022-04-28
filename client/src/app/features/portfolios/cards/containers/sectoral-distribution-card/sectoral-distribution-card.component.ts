import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sectoral-distribution-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-tree-map appFitChart
                           [scheme]="'vivid'"
                           [results]="treemap"
                           [animations]="true"
                           (select)="treemapSelect($event)">
      </ngx-charts-tree-map>
    </ng-container>
  `,
  styleUrls: ['./sectoral-distribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SectoralDistributionCardComponent implements CardContainer<any, any>, OnInit {

  card!: any;
  data$!: Observable<any>;

  testData = [
    {
      "name": "Germany",
      "value": 40632,
      "extra": {
        "code": "de"
      },
      children: [
        {name: 'BetweennessCentrality', value: 3534},
        {name: 'LinkDistance', value: 5731},
        {name: 'MaxFlowMinCut', value: 7840},
        {name: 'ShortestPaths', value: 5914},
        {name: 'SpanningTree', value: 3416}
      ]
    },
    {
      "name": "United States",
      "value": 50000,
      "extra": {
        "code": "us"
      }
    },
    {
      "name": "France",
      "value": 36745,
      "extra": {
        "code": "fr"
      }
    },
    {
      "name": "United Kingdom",
      "value": 36240,
      "extra": {
        "code": "uk"
      }
    }
  ];

  treemap!: any[];
  treemapPath: any[] = [];

  constructor() {
  }

  ngOnInit(): void {
    this.data$.pipe()
      .subscribe(data => {
        this.treemap = data.items;
        this.treemapPath = [];
      });
  }

  treemapSelect(item: any) {
    console.log(item)
    let node;
    if (item.children) {
      // const idx = this.treemapPath.indexOf(item);
      // this.treemapPath.splice(idx + 1);
      this.treemap = item.children;
      return;
    }
    node = this.treemap.find(d => d.name === item.name);
    if (node.children) {
      this.treemapPath.push(node);
      this.treemap = node.children;
    }
  }

}
