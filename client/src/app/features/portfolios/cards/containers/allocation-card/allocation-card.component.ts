import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, ViewEncapsulation } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { AllocatedByOption, AssetAllocationCard } from '../../types/in/asset-allocation-card';

@UntilDestroy()
@Component({
  selector: 'app-allocation-card',
  template: `
    <mat-form-field appearance="legacy">
      <div matPrefix>{{'By:'}}</div>
      <mat-select [value]="card.allocatedBy" (valueChange)="onAllocatedByOptionChanged($event)">
        <mat-option *ngFor="let allocatedBy of allocatedByOptions"
                    [value]="allocatedBy.value">
          {{allocatedBy.value}}
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
      <div class="">{{data.currentTotalValue}}</div>
    </ng-container>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class AllocationCardComponent implements OnInit, CardContainer<AssetAllocationCard, AssetsAllocationCardData> {

  card!: AssetAllocationCard;
  data$!: Observable<AssetsAllocationCardData>;

  cardChanges$ = new EventEmitter<AssetAllocationCard>();

  chartSegments!: any[];

  allocatedByOptions = Object.keys(AllocatedByOption).map(by => ({value: by}));

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

  onAllocatedByOptionChanged(allocatedBy: AllocatedByOption) {
    this.cardChanges$.emit({...this.card, allocatedBy});
  }
}
