import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  OnInit,
  ViewEncapsulation
} from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { AssetsAllocationCardData } from '../../types/out/assets-allocation-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { AllocatedByOption, AssetAllocationCard } from '../../types/in/asset-allocation-card';
import { ECharts, EChartsOption } from 'echarts';
import { CurrencyPipe } from '@angular/common';

@UntilDestroy()
@Component({
  selector: 'app-allocation-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
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
    <div class="breadcrumbs" *ngIf="chartPath.length > 1">
      <ng-container *ngFor="let item of chartPath; let last = last">
        <button mat-button [class.active]="last" [disabled]="last" (click)="selectTopSegments(item)">
          {{ item.name }}
        </button>
        <span *ngIf="!last"> / </span>
      </ng-container>
    </div>
    <div echarts class="pie-chart" [options]="chartOption" [loading]="loading"
         (chartInit)="onChartInit($event)"
         (chartClick)="onSegmentSelected($event)">
    </div>
  `,
  styleUrls: ['./allocation-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class AllocationCardComponent implements OnInit, AfterViewInit, CardContainer<AssetAllocationCard, AssetsAllocationCardData> {

  card!: AssetAllocationCard;
  data$!: Observable<AssetsAllocationCardData>;

  cardChanges$ = new EventEmitter<AssetAllocationCard>();

  chartSegments!: any[];
  chartPath: any[] = [];

  allocatedByOptions = Object.keys(AllocatedByOption).map(by => ({value: by}));

  chartOption!: EChartsOption;
  echartsInstance!: ECharts;
  loading: boolean = false;

  constructor(private cd: ChangeDetectorRef,
              private currencyPipe: CurrencyPipe) {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.chartSegments = [...data.segments];
        this.chartOption = this.constructChartOption(this.chartSegments);
        this.chartPath = [{name: 'All', values: [...this.chartSegments]}];
        this.loading = false;
        this.cd.detectChanges();
      });
  }

  constructChartOption(data: any[]): EChartsOption {
    return {
      tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          console.log(params)
          const price = this.currencyPipe.transform(params.value, params.data.currencySymbol);
          return `<b>${params.marker}${params.name} ${price} (${params.percent}%)</b>`;
        }
      },
      legend: this.getChartLegend(),
      series: [
        {
          name: '',
          type: 'pie',
          center: ['50%', '50%'],
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 32,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data
        }
      ]
    };
  }

  private getChartLegend() {
    if (this.card.rows > 3 || this.card.cols > 3) {
      return {
        bottom: '5%',
        left: 'center'
      }
    }
    return undefined;
  }

  onChartInit(ec: ECharts) {
    this.echartsInstance = ec;
    ec.resize({
      width: this.card.cols * 110,
      height: this.card.rows * 100
    });
  }

  onSegmentSelected(segment: any) {
    const selectedSegment = this.chartSegments.find(s => s.name === segment.name);
    if (selectedSegment.children) {
      this.chartSegments = [...selectedSegment.children];
      this.chartOption = this.constructChartOption(this.chartSegments);
      this.chartPath.push({name: segment.name, values: []});
    }
  }

  onAllocatedByOptionChanged(allocatedBy: AllocatedByOption) {
    this.loading = true;
    this.cd.detectChanges();
    this.card = {...this.card, allocatedBy};
    this.cardChanges$.emit(this.card);
  }

  selectTopSegments(item: any) {
    const idx = this.chartPath.indexOf(item);
    this.chartPath.splice(idx + 1);
    this.chartSegments = [...this.chartPath[idx].values];
    this.chartOption = this.constructChartOption([...this.chartSegments]);
  }
}
