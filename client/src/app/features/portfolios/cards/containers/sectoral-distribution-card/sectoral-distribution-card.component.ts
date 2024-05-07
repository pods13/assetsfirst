import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { SectoralDistributionCardData } from '../../types/out/sectoral-distribution-card-data';
import { DashboardCard } from '../../types/dashboard-card';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import type { ECharts, EChartsOption } from 'echarts';

@UntilDestroy()
@Component({
  selector: 'app-sectoral-distribution-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>
    <div echarts class="distribution-chart" [options]="chartOption" [loading]="loading"
         (chartInit)="onChartInit($event)">
    </div>
  `,
  styleUrls: ['./sectoral-distribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SectoralDistributionCardComponent implements CardContainer<DashboardCard, SectoralDistributionCardData>, OnInit, AfterViewInit {

  card!: DashboardCard;
  data$!: Observable<SectoralDistributionCardData>;

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

  constructChartOption(cardData: SectoralDistributionCardData): EChartsOption {
    return {
      tooltip: {
        trigger: 'item'
      },
      series: [
        {
          type: 'treemap',
          visibleMin: 300,
          label: {
            show: true,
            formatter: '{b}'
          },
          upperLabel: {
            show: true,
            height: 25,
            color: 'white'
          },
          levels: this.getLevelOption(),
          data: cardData.items
        }]
    }
  }

  getLevelOption() {
    return [
      {
        itemStyle: {
          borderColor: '#777',
          borderWidth: 0,
          gapWidth: 1
        },
        upperLabel: {
          show: false
        }
      },
      {
        itemStyle: {
          borderColor: '#555',
          borderWidth: 5,
          gapWidth: 1
        },
        emphasis: {
          itemStyle: {
            borderColor: '#ddd'
          }
        }
      },
      {
        colorSaturation: [0.35, 0.5],
        itemStyle: {
          borderWidth: 2,
          gapWidth: 1,
          borderColorSaturation: 0.6
        }
      }
    ];
  }

  onChartInit(ec: ECharts) {
    this.echartsInstance = ec;
    ec.resize({
      width: this.card.cols * 110,
      height: this.card.rows * 100
    });
  }
}
