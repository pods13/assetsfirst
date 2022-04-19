import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-allocation-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-pie-chart appFitChart
                            [scheme]="'vivid'"
                            [results]="data.segments"
                            [legend]="false"
                            [labels]="false">
      </ngx-charts-pie-chart>
      {{data.totalInvested}}
    </ng-container>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllocationCardComponent implements OnInit, CardContainer<any, any> {

  card!: any;
  data$!: Observable<any>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
