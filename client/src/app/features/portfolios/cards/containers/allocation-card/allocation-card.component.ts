import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-allocation-card',
  template: `
    <div style="position: absolute; top: 50px; left: 50px; right: 50px; bottom: 50px" *ngIf="data$ | async as data">
      <ngx-charts-pie-chart class="chart-container"
                            [scheme]="'vivid'"
                            [results]="data.segments"
                            [legend]="false"
                            [labels]="false">
      </ngx-charts-pie-chart>
    </div>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllocationCardComponent implements OnInit, CardContainer<any> {

  card!: any;
  data$!: Observable<any>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
