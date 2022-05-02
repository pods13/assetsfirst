import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { PortfolioCard } from '../../types/portfolio-card';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';

@Component({
  selector: 'app-allocation-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-pie-chart appFitChart
                            [scheme]="'vivid'"
                            [results]="data.segments"
                            [legend]="false"
                            [labels]="false" (select)="onSegmentSelected($event)">
      </ngx-charts-pie-chart>
      {{data.investedValue + '( ' + data.currentValue + ' )'}}
    </ng-container>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllocationCardComponent implements OnInit, CardContainer<PortfolioCard, AssetsAllocationCardData> {

  card!: PortfolioCard;
  data$!: Observable<AssetsAllocationCardData>;

  constructor() {
  }

  ngOnInit(): void {
  }

  onSegmentSelected(segment: any) {
    console.log(segment)
  }
}
