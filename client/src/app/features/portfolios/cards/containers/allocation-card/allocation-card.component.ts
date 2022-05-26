import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { DashboardCard } from '../../types/dashboard-card';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-allocation-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-pie-chart appFitChart
                            [scheme]="'vivid'"
                            [results]="chartSegments"
                            [legend]="false"
                            [labels]="false" (select)="onSegmentSelected($event)">
      </ngx-charts-pie-chart>
      {{data.investedValue + '( ' + data.currentValue + ' )'}}
    </ng-container>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllocationCardComponent implements OnInit, CardContainer<DashboardCard, AssetsAllocationCardData> {

  card!: DashboardCard;
  data$!: Observable<AssetsAllocationCardData>;

  chartSegments!: any[];

  constructor() {
  }

  ngOnInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.chartSegments = [...data.segments];
      });
  }

  onSegmentSelected(segment: any) {
    const selectedSegment = this.chartSegments.find(s => s.name === segment.name);
    if (selectedSegment.children) {
      this.chartSegments = [...selectedSegment.children];
    }
  }
}
