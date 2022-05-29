import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  OnInit,
  ViewEncapsulation
} from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { DashboardCard } from '../../types/dashboard-card';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { generateId } from '../../helpers/id-generator.helper';

@UntilDestroy()
@Component({
  selector: 'app-allocation-card',
  template: `
    <mat-form-field appearance="legacy">
      <div matPrefix>{{'By:'}}</div>
      <mat-select>
        <mat-option *ngFor="let groupBy of [{value: 'INSTRUMENT_TYPE', viewValue: 'Instrument Type'}]"
                    [value]="groupBy.value">
          {{groupBy.viewValue}}
        </mat-option>
      </mat-select>
    </mat-form-field>
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-pie-chart class="pie-chart clearfix"
        [scheme]="'vivid'"
        [results]="chartSegments"
        [view]="[(card.cols - 2) * 100 + 100, (card.rows - 2) * 100 + 100]"
        [legend]="false"
        [labels]="false" (select)="onSegmentSelected($event)">
      </ngx-charts-pie-chart>
      <div>
        {{data.investedValue + '( ' + data.currentValue + ' )'}}
      </div>
    </ng-container>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class AllocationCardComponent implements OnInit, AfterViewInit, CardContainer<DashboardCard, AssetsAllocationCardData> {

  card!: DashboardCard;
  data$!: Observable<AssetsAllocationCardData>;

  cardChanges$ = new EventEmitter<DashboardCard>();

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

  ngAfterViewInit(): void {

  }
}
