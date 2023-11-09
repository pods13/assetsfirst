import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { AssetDisposalCard } from '../../types/in/asset-disposal-card';
import { AssetDisposalCardData } from '../../types/out/asset-disposal-card-data';
import { ECharts, EChartsOption } from 'echarts';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-asset-disposal-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>
    <div echarts class="disposal-chart" [options]="chartOption" [loading]="loading"
         (chartInit)="onChartInit($event)">
    </div>
  `,
  styleUrls: ['./asset-disposal-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AssetDisposalCardComponent implements OnInit, CardContainer<AssetDisposalCard, AssetDisposalCardData> {

  card!: AssetDisposalCard;
  data$!: Observable<AssetDisposalCardData>;

  chartOption!: EChartsOption;
  echartsInstance!: ECharts;
  loading: boolean = false;

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.chartOption = this.constructChartOption(data);
        this.loading = false;
        this.cd.detectChanges();
      });
  }

  constructChartOption(cardData: any): EChartsOption {
    return {
      tooltip: {
        trigger: 'item'
      },
      xAxis: [
        {
          type: 'value'
        }
      ],
      yAxis: [
        {
          type: 'category',
          axisTick: {
            show: false
          },
          data: [2024]
        }
      ],
      series: [
        {
          name: 'Taxable Income',
          type: 'bar',
          label: {
            show: true,
            position: 'inside'
          },
          emphasis: {
            focus: 'series'
          },
          data: [200]
        },
        {
          name: 'Profit',
          type: 'bar',
          stack: 'Total',
          label: {
            show: true
          },
          emphasis: {
            focus: 'series'
          },
          data: [320]
        },
        {
          name: 'Loss',
          type: 'bar',
          stack: 'Total',
          label: {
            show: true,
            position: 'left'
          },
          emphasis: {
            focus: 'series'
          },
          data: [-120]
        }]
    };
  }

  onChartInit(ec: ECharts) {
    this.echartsInstance = ec;
    ec.resize({
      width: this.card.cols * 110,
      height: this.card.rows * 100
    });
  }
}
