import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, ViewEncapsulation } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { AllocatedByOption, AssetAllocationCard } from '../../types/in/asset-allocation-card';
import { Color } from '@swimlane/ngx-charts';
import { lightColor } from '../../helpers/chart-color-sets';

@UntilDestroy()
@Component({
  selector: 'app-allocation-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card?.title }}</h2>
      <mat-form-field appearance="legacy">
        <div matPrefix>{{'by:'}}</div>
        <mat-select [value]="card.allocatedBy" (valueChange)="onAllocatedByOptionChanged($event)">
          <mat-option *ngFor="let allocatedBy of allocatedByOptions"
                      [value]="allocatedBy.value">
            {{allocatedBy.value}}
          </mat-option>
        </mat-select>
      </mat-form-field>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <div class="breadcrumbs" *ngIf="chartPath.length > 1">
        <ng-container *ngFor="let item of chartPath; let last = last">
          <button mat-button [class.active]="last" [disabled]="last" (click)="selectTopSegments(item)">
            {{ item.name }}
          </button>
          <span *ngIf="!last"> / </span>
        </ng-container>
      </div>
      <ngx-charts-pie-chart class="pie-chart clearfix"
                            [scheme]="colorScheme"
                            [results]="chartSegments"
                            [view]="[(card.cols - 1.5) * 100 + 100, (card.rows - 1.5) * 100 + 100]"
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

  colorScheme = lightColor;

  chartPath!: any[];

  constructor() {
  }

  ngOnInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.chartSegments = [...data.segments];
        this.chartPath = [{name: 'All', values: [...this.chartSegments]}];
      });
  }

  onSegmentSelected(segment: any) {
    const selectedSegment = this.chartSegments.find(s => s.name === segment.name);
    if (selectedSegment.children) {
      this.chartSegments = [...selectedSegment.children];
      this.chartPath.push({name: segment.name, values: []});
    }
  }

  onAllocatedByOptionChanged(allocatedBy: AllocatedByOption) {
    this.cardChanges$.emit({...this.card, allocatedBy});
  }

  selectTopSegments(item: any) {
    const idx = this.chartPath.indexOf(item);
    this.chartPath.splice(idx + 1);
    this.chartSegments = [...this.chartPath[idx].values];
  }
}
